package kr.tekit.lion.presentation.main.vm

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.AreaCodeList
import kr.tekit.lion.domain.model.ListSearchOption
import kr.tekit.lion.domain.model.SigunguCodeList
import kr.tekit.lion.domain.model.onError
import kr.tekit.lion.domain.model.onSuccess
import kr.tekit.lion.domain.repository.AreaCodeRepository
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.SigunguCodeRepository
import kr.tekit.lion.presentation.base.BaseViewModel
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.PlaceModel
import kr.tekit.lion.presentation.main.model.ScreenState
import kr.tekit.lion.presentation.main.model.SortByLatest
import kr.tekit.lion.presentation.main.model.UiEvent
import kr.tekit.lion.presentation.main.model.VisualImpairment
import kr.tekit.lion.presentation.main.model.toUiModel
import java.util.TreeSet
import javax.inject.Inject

@HiltViewModel
class SearchMainViewModel @Inject constructor(
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
    private val placeRepository: PlaceRepository,
) : BaseViewModel() {

    init {
        viewModelScope.launch {
            loadAreaCodes()
            loadPlaces()
        }
    }

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.List)
    val screenState: StateFlow<ScreenState> get() = _screenState.asStateFlow()

    private val _option = MutableStateFlow(initOption())
    val option get() = _option.asStateFlow()

    private val _optionState = MutableStateFlow<Map<DisabilityType, Int>>(emptyMap())
    val optionState get() = _optionState.asStateFlow()

    private val _areaCode = MutableStateFlow(AreaCodeList(emptyList()))
    val areaCode get() = _areaCode.asStateFlow()

    private val _sigunguCode = MutableStateFlow(SigunguCodeList(emptyList()))
    val sigunguCode get() = _sigunguCode.asStateFlow()

    // BottomSheet 에서 선택된 항목을 들을 유지하기 위한 layout ID
    private val _physicalDisabilityOptions = MutableStateFlow<List<Int>>(emptyList())
    val physicalDisabilityOptions get() = _physicalDisabilityOptions.asStateFlow()

    private val _hearingImpairmentOptions = MutableStateFlow<List<Int>>(emptyList())
    val hearingImpairmentOptions get() = _hearingImpairmentOptions.asStateFlow()

    private val _visualImpairmentOptions = MutableStateFlow<List<Int>>(emptyList())
    val visualImpairmentOptions get() = _visualImpairmentOptions.asStateFlow()

    private val _infantFamilyOptions = MutableStateFlow<List<Int>>(emptyList())
    val infantFamilyOptions get() = _infantFamilyOptions.asStateFlow()

    private val _elderlyPersonOptions = MutableStateFlow<List<Int>>(emptyList())
    val elderlyPersonOptions get() = _elderlyPersonOptions.asStateFlow()

    private val _place = MutableStateFlow<List<PlaceModel>>(emptyList())
    val place get() = _place.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage get() = _isLastPage.asStateFlow()

    private val _uiEvent = MutableStateFlow<UiEvent?>(null)
    val uiEvent: StateFlow<UiEvent?> get() = _uiEvent.asStateFlow()

    fun changeScreenState(state: ScreenState) {
        _screenState.value = state
    }

    fun onSelectedTab(category: String) {
        _place.value.toMutableList().clear()
        _option.value = _option.value.copy(category = category, page = 0)
        _uiEvent.value = UiEvent.TabChanged
        _uiEvent.value = null
    }

    fun onSelectedSigungu(sigunguName: String) {
        _place.value.toMutableList().clear()
        val sigunguCode = _sigunguCode.value.findSigunguCode(sigunguName)
        _option.value = _option.value.copy(sigunguCode = sigunguCode, page = 0)
        _isLastPage.value = false
    }

    fun onSelectedArrange(arrange: String) {
        _place.value.toMutableList().clear()
        _option.value = _option.value.copy(arrange = arrange, page = 0)
        _isLastPage.value = false
    }

    fun onSelectOption(optionIds: List<Int>, optionCodes: List<Long>, type: DisabilityType) {
        _place.value.toMutableList().clear()
        val updatedTypes = _option.value.disabilityType ?: TreeSet()
        val updatedFilters = _option.value.detailFilter ?: TreeSet()

        updateOptionState(type, optionIds.size)
        when (type) {
            is PhysicalDisability -> {
                updatedFilters.removeAll(PhysicalDisability.filterCodes)
                _physicalDisabilityOptions.value = optionIds
            }
            is VisualImpairment -> {
                updatedFilters.removeAll(VisualImpairment.filterCodes)
                _visualImpairmentOptions.value = optionIds
            }
            is HearingImpairment -> {
                updatedFilters.removeAll(HearingImpairment.filterCodes)
                _hearingImpairmentOptions.value = optionIds
            }
            is InfantFamily -> {
                updatedFilters.removeAll(InfantFamily.filterCodes)
                _infantFamilyOptions.value = optionIds
            }
            is ElderlyPeople -> {
                updatedFilters.removeAll(ElderlyPeople.filterCodes)
                _elderlyPersonOptions.value = optionIds
            }
        }

        if (optionCodes.isNotEmpty()) {
            updatedTypes.add(type.code)
            updatedFilters.addAll(optionCodes)
        } else {
            updatedTypes.remove(type.code)
        }

        _option.value = _option.value.copy(
            disabilityType = updatedTypes,
            detailFilter = updatedFilters,
            page = 0
        )
        _isLastPage.value = false
    }

    private fun updateOptionState(disabilityType: DisabilityType, cnt: Int) {
        val currentOption = optionState.value.toMutableMap()
        currentOption[disabilityType] = cnt
        _optionState.value = currentOption.toMap()
    }

    suspend fun onSelectedArea(areaName: String) {
        _place.value.toMutableList().clear()
        val areaCode = _areaCode.value.findAreaCode(areaName) ?: ""
        _option.value = _option.value.copy(areaCode = areaCode)
        viewModelScope.launch {
            _sigunguCode.value = sigunguCodeRepository.getAllSigunguCode(areaCode)
        }
        _isLastPage.value = false
    }

    fun whenLastPageReached() {
        val currentOption = option.value
        val nextOption = currentOption.copy(page = currentOption.page + 1)

        viewModelScope.launch {
            placeRepository.getSearchPlaceResultByList(nextOption)
                .onSuccess { result ->
                    _place.value += result.toUiModel()
                    _option.value = nextOption
                    if (result.isLastPage) _isLastPage.value = true
                }.onError {
                    handleNetworkError(it)
                }
        }
    }

    private suspend fun loadAreaCodes() {
        val areaCodes = areaCodeRepository.getAllAreaCodes()
        _areaCode.value = areaCodes
    }

    private suspend fun loadPlaces() {
        option.collect {
            if (it.page == 0) {
                placeRepository.getSearchPlaceResultByList(it)
                    .onSuccess { result ->
                        _place.value = result.toUiModel()
                        if (result.isLastPage or result.places.isEmpty()) {
                            _isLastPage.value = true
                        }
                    }.onError { e ->
                        handleNetworkError(e)
                    }
            }
        }
    }

    fun onClickResetIcon() {
        _option.value = _option.value.copy(
            disabilityType = TreeSet(),
            detailFilter = TreeSet()
        )

        _physicalDisabilityOptions.value = emptyList()
        _hearingImpairmentOptions.value = emptyList()
        _visualImpairmentOptions.value = emptyList()
        _infantFamilyOptions.value = emptyList()
        _elderlyPersonOptions.value = emptyList()

    }

    private fun initOption(): ListSearchOption {
        return ListSearchOption(
            category = Category.PLACE.name,
            size = 0,
            page = 0,
            arrange = SortByLatest.sortCode
        )
    }
}
