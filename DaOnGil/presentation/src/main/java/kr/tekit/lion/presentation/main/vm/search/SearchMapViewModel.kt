package kr.tekit.lion.presentation.main.vm.search

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.presentation.base.BaseViewModel
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.Locate
import kr.tekit.lion.presentation.main.model.MapOptionState
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.PlaceModel
import kr.tekit.lion.presentation.main.model.SortByLatest
import kr.tekit.lion.presentation.main.model.VisualImpairment
import java.util.TreeSet
import javax.inject.Inject

@HiltViewModel
class SearchMapViewModel  @Inject constructor(
    private val placeRepository: PlaceRepository,
) : BaseViewModel(){

    private val _place = MutableStateFlow<List<PlaceModel>>(emptyList())
    val place get() = _place.asStateFlow()

    private val _mapOptionState = MutableStateFlow(initMapOption())
    val mapOptionState get() = _mapOptionState.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val mapSearchResult = _mapOptionState
        .debounce(DEBOUNCE_INTERVAL)
        .flatMapLatest { request ->
            placeRepository.getSearchPlaceResultByMap(request.toDomainModel())
        }.flowOn(Dispatchers.IO)
        .catch { e: Throwable ->
            e.printStackTrace()
        }

    fun onSelectedTab(category: Category) {
        _place.value.toMutableList().clear()
        _mapOptionState.value = _mapOptionState.value.copy(category = category)
    }

    fun onCameraPositionChanged(locate: Locate){
        _mapOptionState.value = _mapOptionState.value.copy(location = locate)
    }

    fun onSelectOption(optionCodes: List<Long>, type: DisabilityType) {
        _place.update { emptyList() }

        val currentOptionState = _mapOptionState.value
        val mapUpdatedTypes = TreeSet(currentOptionState.disabilityType)
        val mapUpdatedFilters = TreeSet(currentOptionState.detailFilter)

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

        _mapOptionState.update{
            _mapOptionState.value.copy(
                disabilityType = mapUpdatedTypes,
                detailFilter = mapUpdatedFilters,
            )
        }
    }


    private fun initMapOption(): MapOptionState {
        return MapOptionState(
            category = Category.PLACE,
            location = Locate(
                minLatitude = 0.0,
                maxLatitude = 0.0,
                minLongitude = 0.0,
                maxLongitude = 0.0,
            ),
            disabilityType = DisabilityType.createDisabilityTypeCodes(),
            detailFilter = DisabilityType.createFilterCodes(),
            arrange = SortByLatest.sortCode
        )
    }

    companion object{
        private const val DEBOUNCE_INTERVAL = 1500L
    }
}