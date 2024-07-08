package kr.tekit.lion.presentation.main.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.AreaCode
import kr.tekit.lion.domain.model.ListSearchOption
import kr.tekit.lion.domain.model.SigunguCode
import kr.tekit.lion.domain.model.SigunguList
import kr.tekit.lion.domain.repository.AreaCodeRepository
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.SigunguCodeRepository
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.PlaceModel
import kr.tekit.lion.presentation.main.model.ScreenState
import kr.tekit.lion.presentation.main.model.SortByLatest
import kr.tekit.lion.presentation.main.model.toUiModel
import java.util.TreeSet
import javax.inject.Inject

@HiltViewModel
class SearchMainViewModel @Inject constructor(
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            _areaCode.value = areaCodeRepository.getAllAreaCodes()
            loadPlaces()
        }
    }

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.List)
    val screenState: StateFlow<ScreenState> get() = _screenState.asStateFlow()

    private val _option = MutableStateFlow(ListSearchOption(
            category = Category.PLACE.name,
            size = 0,
            page = 0,
            arrange = SortByLatest.sortCoed
        )
    )
    val option get() = _option.asStateFlow()

    private val _optionState = MutableStateFlow<Map<DisabilityType, Int>>(emptyMap())
    val optionState get() = _optionState.asStateFlow()

    private val _areaCode = MutableStateFlow<List<AreaCode>>(emptyList())
    val areaCode get() = _areaCode.asStateFlow()

    private val _sigunguCode = MutableStateFlow(SigunguList(emptyList()))
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

    private suspend fun loadPlaces() {
        option.collect{
            val searchResult = placeRepository.getSearchPlaceResultByList(it)

            searchResult.onSuccess { result ->
                val newPlaceModels = result.toUiModel()
                _place.value = newPlaceModels
            }.onFailure {
                // 에러 처리 로직을 추가할 수 있습니다.
            }
        }
    }

    fun changeScreenState(state: ScreenState) {
        _screenState.value = state
    }

    fun onSelectedTab(category: String) {
        _option.value = _option.value.copy(category = category)
    }

    fun onSelectedSigungu(sigunguName: String) {
        val sigunguCode = _sigunguCode.value.findSigunguCode(sigunguName)
        _option.value = _option.value.copy(sigunguCode = sigunguCode)
    }

    fun onSelectedArrange(arrange: String) {
        _option.value = _option.value.copy(arrange = arrange)
    }

    fun onSelectOption(optionIds: List<Int>, optionCodes: List<Long>, type: DisabilityType) {
        val updatedTypes = _option.value.disabilityType ?: TreeSet()
        val updatedOptions = _option.value.detailFilter?.toMutableSet() ?: mutableSetOf()

        updatedOptions.clear()
        updatedTypes.clear()
        updateOptionState(type, optionIds.size)

        if(optionIds.isNotEmpty()) {
            when (type) {
                is DisabilityType.PhysicalDisability -> updatedTypes.add(DisabilityType.PhysicalDisability.type)
                is DisabilityType.VisualImpairment -> updatedTypes.add(DisabilityType.VisualImpairment.type)
                is DisabilityType.HearingImpairment -> updatedTypes.add(DisabilityType.HearingImpairment.type)
                is DisabilityType.InfantFamily -> updatedTypes.add(DisabilityType.InfantFamily.type)
                is DisabilityType.ElderlyPeople -> updatedTypes.add(DisabilityType.ElderlyPeople.type)
            }
            optionCodes.map { updatedOptions.add(it) }
        }

        when (type) {
            is DisabilityType.PhysicalDisability -> _physicalDisabilityOptions.value = optionIds
            is DisabilityType.VisualImpairment -> _visualImpairmentOptions.value = optionIds
            is DisabilityType.HearingImpairment -> _hearingImpairmentOptions.value = optionIds
            is DisabilityType.InfantFamily -> _infantFamilyOptions.value = optionIds
            is DisabilityType.ElderlyPeople -> _elderlyPersonOptions.value = optionIds
        }

        _option.value = _option.value.copy(
            disabilityType = updatedTypes,
            detailFilter = updatedOptions
        )
    }

    private fun updateOptionState(disabilityType: DisabilityType, cnt: Int) {
        val currentOption = optionState.value.toMutableMap()
        currentOption[disabilityType] = cnt
        _optionState.value = currentOption.toMap()
    }

    fun onSelectedArea(areaName: String) = viewModelScope.launch{
        _areaCode.value.find { it.name == areaName }?.let { areaCode ->
            _option.value = _option.value.copy(areaCode = areaCode.code)
            _sigunguCode.value = sigunguCodeRepository.getAllSigunguCode(areaCode.code)
        }
    }

    fun onClickSearchButton() = viewModelScope.launch {
        option.collect { option ->
            val searchResult = placeRepository.getSearchPlaceResultByList(option)
            searchResult.onSuccess { result ->
                val newPlaceModels = result.toUiModel()
                val updatedPlaceList = _place.value + newPlaceModels
                _place.value = updatedPlaceList
            }.onFailure {
                Log.d("MyOkhttpResult", it.toString())
                Log.d("MyOkhttpResult", it.message.toString())
                Log.d("MyOkhttpResult", it.localizedMessage.toString())
                Log.d("MyOkhttpResult", it.cause.toString())
            }
        }
    }


    fun whenLastPageReached() = viewModelScope.launch {
        _option.value = _option.value.copy(
            page = _option.value.page + 1
        )

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
}
