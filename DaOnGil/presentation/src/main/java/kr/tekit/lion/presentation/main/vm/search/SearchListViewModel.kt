package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.AreaCodeList
import kr.tekit.lion.domain.model.SigunguCodeList
import kr.tekit.lion.domain.model.onError
import kr.tekit.lion.domain.model.onSuccess
import kr.tekit.lion.domain.repository.AreaCodeRepository
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.SigunguCodeRepository
import kr.tekit.lion.presentation.delegate.ViewModelDelegate
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.ListOptionState
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.PlaceModel
import kr.tekit.lion.presentation.main.model.SortByLatest
import kr.tekit.lion.presentation.main.model.VisualImpairment
import kr.tekit.lion.presentation.main.model.toUiModel
import java.util.TreeSet
import javax.inject.Inject

@HiltViewModel
class SearchListViewModel @Inject constructor(
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
    private val placeRepository: PlaceRepository,
): ViewModel() {

    @Inject
    lateinit var viewModelDelegate: ViewModelDelegate

    init {
        viewModelScope.launch {
            loadAreaCodes()
            loadPlaces()
        }
    }

    val errorMessage: StateFlow<String?> get() = viewModelDelegate.errorMessage

    private val _areaCode = MutableStateFlow(AreaCodeList(emptyList()))
    val areaCode get() = _areaCode.asStateFlow()

    private val _sigunguCode = MutableStateFlow(SigunguCodeList(emptyList()))
    val sigunguCode get() = _sigunguCode.asStateFlow()

    private val _listOptionState = MutableStateFlow(initListOption())
    val listOptionState get() = _listOptionState.asStateFlow()

    private val _place = MutableStateFlow<List<PlaceModel>>(emptyList())
    val place get() = _place.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage get() = _isLastPage.asStateFlow()

    fun onSelectOption(optionCodes: List<Long>, type: DisabilityType) {
        _place.update { emptyList() }

        val currentOptionState = _listOptionState.value
        val updatedDisabilityTypes = TreeSet(currentOptionState.disabilityType)
        val updatedDetailFilters = TreeSet(currentOptionState.detailFilter)

        when (type) {
            is PhysicalDisability -> updatedDetailFilters.removeAll(PhysicalDisability.filterCodes)
            is VisualImpairment -> updatedDetailFilters.removeAll(VisualImpairment.filterCodes)
            is HearingImpairment -> updatedDetailFilters.removeAll(HearingImpairment.filterCodes)
            is InfantFamily -> updatedDetailFilters.removeAll(InfantFamily.filterCodes)
            is ElderlyPeople -> updatedDetailFilters.removeAll(ElderlyPeople.filterCodes)
        }

        if (optionCodes.isNotEmpty()) {
            updatedDisabilityTypes.add(type.code)
            updatedDetailFilters.addAll(optionCodes)
        } else {
            updatedDisabilityTypes.remove(type.code)
        }

        _listOptionState.update {
            currentOptionState.copy(
                disabilityType = updatedDisabilityTypes,
                detailFilter = updatedDetailFilters,
                page = 0
            )
        }
        _isLastPage.value = false
    }

    fun onSelectedTab(category: Category) {
        _place.update { emptyList() }
        _listOptionState.update {
            _listOptionState.value.copy(category = category, page = 0)
        }
    }

    private suspend fun loadPlaces(){
        listOptionState.collect{
            placeRepository.getSearchPlaceResultByList(it.toDomainModel())
                .onSuccess { result ->
                _place.update { _place.value + result.toUiModel() }
                if (result.isLastPage) _isLastPage.value = true
            }.onError { e ->
                viewModelDelegate.handleNetworkError(e)
            }
        }
    }

    suspend fun onSelectedArea(areaName: String) {
        _place.update { emptyList() }
        val areaCode = _areaCode.value.findAreaCode(areaName) ?: ""
        _listOptionState.update {
            _listOptionState.value.copy(areaCode = areaCode)
        }
        viewModelScope.launch {
            _sigunguCode.value = sigunguCodeRepository.getAllSigunguCode(areaCode)
        }
        _isLastPage.value = false
    }

    fun onSelectedSigungu(sigunguName: String) {
        _place.update { emptyList() }
        val sigunguCode = _sigunguCode.value.findSigunguCode(sigunguName)
        _listOptionState.update {
            _listOptionState.value.copy(sigunguCode = sigunguCode, page = 0)
        }
        _isLastPage.value = false
    }

    fun onSelectedArrange(arrange: String) {
        _place.update { emptyList() }
        _listOptionState.update {
            _listOptionState.value.copy(arrange = arrange, page = 0)
        }
        _isLastPage.value = false
    }

    fun whenLastPageReached() {
        val currentOption = listOptionState.value
        _listOptionState.update {
            currentOption.copy(page = currentOption.page + 1)
        }
    }

    private suspend fun loadAreaCodes() {
        _areaCode.update { areaCodeRepository.getAllAreaCodes() }
    }

    private fun initListOption(): ListOptionState {
        return ListOptionState(
            category = Category.PLACE,
            page = 0,
            disabilityType = TreeSet(),
            detailFilter = TreeSet(),
            areaCode = null,
            sigunguCode = null,
            arrange = SortByLatest.sortCode
        )
    }
}