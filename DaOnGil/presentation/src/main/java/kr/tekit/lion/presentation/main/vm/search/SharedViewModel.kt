package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.SharedOptionState
import kr.tekit.lion.presentation.main.model.VisualImpairment
import java.util.TreeSet

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

    private val _bottomSheetOptionState = MutableStateFlow<Map<DisabilityType, Int>>(emptyMap())
    val bottomSheetOptionState get() = _bottomSheetOptionState.asStateFlow()

    private val _sharedOptionState = MutableStateFlow(SharedOptionState())
    val sharedOptionState get() = _sharedOptionState.asStateFlow()

    private val _tabState = MutableStateFlow(Category.PLACE)
    val tabState get() = _tabState.asStateFlow()

    fun onSelectOption(optionIds: List<Int>, type: DisabilityType, optionCodes: List<Long>) {
        updateBottomSheetOptionState(type, optionIds.size)

        val currentOptionState = _sharedOptionState.value
        val updatedDisabilityTypes = TreeSet(currentOptionState.disabilityType)
        val updatedDetailFilters = TreeSet(currentOptionState.detailFilter)

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

        _sharedOptionState.update {
            currentOptionState.copy(
                disabilityType = updatedDisabilityTypes,
                detailFilter = updatedDetailFilters,
            )
        }
    }

    fun onTabChanged(category: Category) {
        _tabState.value = category
    }

    private fun updateBottomSheetOptionState(disabilityType: DisabilityType, cnt: Int) {
        val currentOption = _bottomSheetOptionState.value.toMutableMap()
        currentOption[disabilityType] = cnt
        _bottomSheetOptionState.value = currentOption.toMap()
    }

    fun onClickResetIcon() {
        _physicalDisabilityOptions.update { emptyList() }
        _hearingImpairmentOptions.update { emptyList() }
        _visualImpairmentOptions.update { emptyList() }
        _infantFamilyOptions.update { emptyList() }
        _elderlyPersonOptions.update { emptyList() }
        _bottomSheetOptionState.update { emptyMap() }
        _sharedOptionState.update { SharedOptionState().clear() }
    }
}