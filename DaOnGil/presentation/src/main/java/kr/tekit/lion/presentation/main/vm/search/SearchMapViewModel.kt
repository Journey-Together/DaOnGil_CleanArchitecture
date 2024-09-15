package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.NetworkError
import kr.tekit.lion.domain.exception.TimeoutError
import kr.tekit.lion.domain.exception.UnknownError
import kr.tekit.lion.domain.exception.UnknownHostError
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.Locate
import kr.tekit.lion.presentation.main.model.MapOptionState
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.SharedOptionState
import kr.tekit.lion.presentation.main.model.SortByLatest
import kr.tekit.lion.presentation.main.model.VisualImpairment
import kr.tekit.lion.presentation.main.model.toUiModel
import java.net.UnknownHostException
import java.util.TreeSet
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class SearchMapViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
) : ViewModel(){

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState get() = networkErrorDelegate.networkState

    private val _mapOptionState = MutableStateFlow(MapOptionState.create())
    val mapOptionState get() = _mapOptionState.asStateFlow()

    private val _searchState = MutableSharedFlow<Boolean>()
    val searchState get() = _searchState.asSharedFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val mapSearchResult = _mapOptionState
        .debounce(DEBOUNCE_INTERVAL)
        .flatMapLatest { request ->
            val response = placeRepository.getSearchPlaceResultByMap(request.toDomainModel())
            networkErrorDelegate.handleNetworkSuccess()
            response.map {
                if (it.places.isEmpty()) _searchState.emit(false)
                it.toUiModel()
            }
        }.flowOn(Dispatchers.IO)
        .catch { e: Throwable ->
            submitThrowableState(e)
        }

    fun onSelectedTab(category: Category) {
        networkErrorDelegate.handleNetworkLoading()

        if (_mapOptionState.value.category != category) {
            _mapOptionState.update { it.copy(category = category) }
        }
    }

    fun onCameraPositionChanged(locate: Locate){
        networkErrorDelegate.handleNetworkLoading()

        if (_mapOptionState.value.location != locate) {
            _mapOptionState.update { it.copy(location = locate) }
        }
    }

    fun onChangeMapState(state: SharedOptionState){
        networkErrorDelegate.handleNetworkLoading()

        _mapOptionState.update {
            if (state.detailFilter.isEmpty()){
                it.copy(
                    disabilityType = TreeSet(),
                    detailFilter = TreeSet()
                )
            }else{
                it.copy(
                    disabilityType = state.disabilityType,
                    detailFilter = state.detailFilter
                )
            }
        }
    }

    fun onSelectOption(optionCodes: List<Long>, type: DisabilityType) {
        networkErrorDelegate.handleNetworkLoading()

        val currentOptionState = _mapOptionState.value
        val mapUpdatedTypes = TreeSet(currentOptionState.disabilityType)
        val mapUpdatedFilters = TreeSet(currentOptionState.detailFilter)

        viewModelScope.launch {
            when (type) {
                is PhysicalDisability -> {
                    mapUpdatedFilters.removeAll(PhysicalDisability.filterCodes)
                }

                is VisualImpairment -> {
                    mapUpdatedFilters.removeAll(VisualImpairment.filterCodes)
                }

                is HearingImpairment -> {
                    mapUpdatedFilters.removeAll(HearingImpairment.filterCodes)
                }

                is InfantFamily -> {
                    mapUpdatedFilters.removeAll(InfantFamily.filterCodes)
                }

                is ElderlyPeople -> {
                    mapUpdatedFilters.removeAll(ElderlyPeople.filterCodes)
                }
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
    }

    fun onClickRestButton(){
        _mapOptionState.update { it.copy(
            disabilityType = DisabilityType.createDisabilityTypeCodes(),
            detailFilter = DisabilityType.createFilterCodes(),
        ) }
    }

    private fun submitThrowableState(e: Throwable){
        when (e) {
            is TimeoutException ->{
                networkErrorDelegate.handleNetworkError(TimeoutError)
            }
            is UnknownHostException -> {
                networkErrorDelegate.handleNetworkError(UnknownHostError)
            }
            is UnknownError -> {
                networkErrorDelegate.handleNetworkError(UnknownError)
            }
            else -> {
                networkErrorDelegate.handleNetworkError(e as NetworkError)
            }
        }
    }

    companion object{
        private const val DEBOUNCE_INTERVAL = 1500L
    }
}