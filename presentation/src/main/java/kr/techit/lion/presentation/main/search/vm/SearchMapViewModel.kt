package kr.techit.lion.presentation.main.search.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.delegate.NetworkEventDelegate
import kr.techit.lion.presentation.main.search.vm.model.Category
import kr.techit.lion.presentation.main.search.vm.model.DisabilityType
import kr.techit.lion.presentation.main.search.vm.model.ElderlyPeople
import kr.techit.lion.presentation.main.search.vm.model.HearingImpairment
import kr.techit.lion.presentation.main.search.vm.model.InfantFamily
import kr.techit.lion.presentation.main.search.vm.model.Locate
import kr.techit.lion.presentation.main.search.vm.model.MapOptionState
import kr.techit.lion.presentation.main.search.vm.model.PhysicalDisability
import kr.techit.lion.presentation.main.search.vm.model.SharedOptionState
import kr.techit.lion.presentation.main.search.vm.model.VisualImpairment
import kr.techit.lion.presentation.main.search.vm.model.toUiModel
import java.util.TreeSet
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchMapViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val networkEventDelegate: NetworkEventDelegate
) : BaseViewModel() {

    val networkEvent get() = networkEventDelegate.event

    private val _mapOptionState = MutableStateFlow(MapOptionState.create())
    val mapOptionState get() = _mapOptionState.asStateFlow()

    private val _searchState = MutableSharedFlow<Boolean>()
    val searchState get() = _searchState.asSharedFlow()

    val mapSearchResult = _mapOptionState
        .debounce(DEBOUNCE_INTERVAL)
        .flatMapLatest { request ->
            val response = placeRepository.getSearchPlaceResultByMap(request.toDomainModel())
            networkEventDelegate.emitEvent(
                scope = viewModelScope,
                event = NetworkEvent.Success
            )
            response.map {
                if (it.places.isEmpty()) _searchState.emit(false)
                it.toUiModel()
            }
        }.flowOn(recordExceptionHandler)
        .catch { e: Throwable ->
            networkEventDelegate.handleErrorEvent(viewModelScope, e)
        }

    fun onSelectedTab(category: Category) {
        networkEventDelegate.emitEvent(viewModelScope, NetworkEvent.Loading)

        if (_mapOptionState.value.category != category) {
            _mapOptionState.update { it.copy(category = category) }
        }
    }

    fun onCameraPositionChanged(locate: Locate) {
        networkEventDelegate.emitEvent(viewModelScope, NetworkEvent.Loading)

        if (_mapOptionState.value.location != locate) {
            _mapOptionState.update { it.copy(location = locate) }
        }
    }

    fun onChangeMapState(state: SharedOptionState) {
        networkEventDelegate.emitEvent(viewModelScope, NetworkEvent.Loading)

        _mapOptionState.update {
            if (state.detailFilter.isEmpty()) {
                it.copy(
                    disabilityType = TreeSet(),
                    detailFilter = TreeSet()
                )
            } else {
                it.copy(
                    disabilityType = state.disabilityType,
                    detailFilter = state.detailFilter
                )
            }
        }
    }

    fun onSelectOption(optionCodes: List<Long>, type: DisabilityType) {
        networkEventDelegate.emitEvent(viewModelScope, NetworkEvent.Loading)

        val currentOptionState = _mapOptionState.value
        val mapUpdatedTypes = TreeSet(currentOptionState.disabilityType)
        val mapUpdatedFilters = TreeSet(currentOptionState.detailFilter)

        when (type) {
            is PhysicalDisability -> mapUpdatedFilters.removeAll(PhysicalDisability.filterCodes)
            is VisualImpairment -> mapUpdatedFilters.removeAll(VisualImpairment.filterCodes)
            is HearingImpairment -> mapUpdatedFilters.removeAll(HearingImpairment.filterCodes)
            is InfantFamily -> mapUpdatedFilters.removeAll(InfantFamily.filterCodes)
            is ElderlyPeople -> mapUpdatedFilters.removeAll(ElderlyPeople.filterCodes)
        }

        if (optionCodes.isNotEmpty()) {
            mapUpdatedTypes.add(type.code)
            mapUpdatedFilters.addAll(optionCodes)
        } else {
            mapUpdatedTypes.remove(type.code)
        }

        _mapOptionState.update {
            _mapOptionState.value.copy(
                disabilityType = mapUpdatedTypes,
                detailFilter = mapUpdatedFilters,
            )
        }
    }

    fun onClickRestButton() {
        _mapOptionState.update {
            it.copy(
                disabilityType = DisabilityType.createDisabilityTypeCodes(),
                detailFilter = DisabilityType.createFilterCodes(),
            )
        }
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 1000L
    }
}
