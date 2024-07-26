package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.VisualImpairment

class SharedViewModel: ViewModel() {
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

    private val _optionState = MutableStateFlow<Map<DisabilityType, Int>>(emptyMap())
    val optionState get() = _optionState.asStateFlow()

    private val _tabState = MutableStateFlow(Category.PLACE)
    val tabState get() = _tabState.asStateFlow()

    fun onSelectOption(optionIds: List<Int>, type: DisabilityType) {
        updateOptionState(type, optionIds.size)
        when (type) {
            is PhysicalDisability -> {
                _physicalDisabilityOptions.value = optionIds
            }
            is VisualImpairment -> {
                _visualImpairmentOptions.value = optionIds
            }
            is HearingImpairment -> {
                _hearingImpairmentOptions.value = optionIds
            }
            is InfantFamily -> {
                _infantFamilyOptions.value = optionIds
            }
            is ElderlyPeople -> {
                _elderlyPersonOptions.value = optionIds
            }
        }
    }

    fun onTabChanged(category: Category) {
        _tabState.value = category
    }

    private fun updateOptionState(disabilityType: DisabilityType, cnt: Int) {
        val currentOption = optionState.value.toMutableMap()
        currentOption[disabilityType] = cnt
        _optionState.value = currentOption.toMap()
    }

    fun onClickResetIcon() {
        _physicalDisabilityOptions.value = emptyList()
        _hearingImpairmentOptions.value = emptyList()
        _visualImpairmentOptions.value = emptyList()
        _infantFamilyOptions.value = emptyList()
        _elderlyPersonOptions.value = emptyList()
    }
}