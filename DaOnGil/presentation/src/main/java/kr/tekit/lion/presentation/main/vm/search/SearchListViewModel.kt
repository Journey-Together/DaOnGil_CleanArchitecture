package kr.tekit.lion.presentation.main.vm.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.model.area.AreaCodeList
import kr.tekit.lion.domain.model.area.SigunguCodeList
import kr.tekit.lion.domain.model.onError
import kr.tekit.lion.domain.model.onSuccess
import kr.tekit.lion.domain.repository.AreaCodeRepository
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.SigunguCodeRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.ArrangeState
import kr.tekit.lion.presentation.main.model.Category
import kr.tekit.lion.presentation.main.model.CategoryModel
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.ListOptionState
import kr.tekit.lion.presentation.main.model.ListSearchUIModel
import kr.tekit.lion.presentation.main.model.NoPlaceModel
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.PlaceModel
import kr.tekit.lion.presentation.main.model.SharedOptionState
import kr.tekit.lion.presentation.main.model.SigunguModel
import kr.tekit.lion.presentation.main.model.SortByLatest
import kr.tekit.lion.presentation.main.model.SortModel
import kr.tekit.lion.presentation.main.model.VisualImpairment
import kr.tekit.lion.presentation.main.model.toUiModel
import java.util.TreeSet
import javax.inject.Inject
import kotlin.collections.removeAll

@HiltViewModel
class SearchListViewModel @Inject constructor(
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
    private val placeRepository: PlaceRepository,
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    init {
        viewModelScope.launch {
            mapChanged.collect { changed ->
                if (changed) reloadPlace()
            }
        }

        viewModelScope.launch {
            loadAreaCodes()
            loadPlaces()
        }
    }

    val errorMessage: StateFlow<String?> get() = networkErrorDelegate.errorMessage

    private val _uiState: MutableStateFlow<List<ListSearchUIModel>> = MutableStateFlow(
        listOf(
            CategoryModel(mutableMapOf()),
            AreaModel(emptyList()),
            SortModel(0)
        )
    )
    val uiState: StateFlow<List<ListSearchUIModel>> = _uiState.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage get() = _isLastPage.asStateFlow()

    private val listOptionState = MutableStateFlow(initListOption())

    private val areaCode = MutableStateFlow(AreaCodeList(emptyList()))

    private val sigunguCode = MutableStateFlow(SigunguCodeList(emptyList()))

    private val mapChanged = MutableSharedFlow<Boolean>()

    fun onSelectOption(optionCodes: List<Long>, type: DisabilityType) {
        viewModelScope.launch(Dispatchers.IO) {
            clearPlace()
            val updatedOptionState = updateListOptionState(optionCodes, type)
            listOptionState.update { updatedOptionState }

            val updatedUiState = updateUiStateWithOptionCount(optionCodes, type)
            _uiState.update { updatedUiState }
        }
    }

    private fun updateListOptionState(optionCodes: List<Long>, type: DisabilityType): ListOptionState {
        val currentOptionState = listOptionState.value
        val updatedDisabilityTypes = TreeSet(currentOptionState.disabilityType)
        val updatedDetailFilters = TreeSet(currentOptionState.detailFilter)

        return when (type) {
            is PhysicalDisability -> updateOptionState(
                updatedDisabilityTypes,
                updatedDetailFilters,
                optionCodes,
                PhysicalDisability.filterCodes,
                type
            )

            is VisualImpairment -> updateOptionState(
                updatedDisabilityTypes,
                updatedDetailFilters,
                optionCodes,
                VisualImpairment.filterCodes,
                type
            )

            is HearingImpairment -> updateOptionState(
                updatedDisabilityTypes,
                updatedDetailFilters,
                optionCodes,
                HearingImpairment.filterCodes,
                type
            )

            is InfantFamily -> updateOptionState(
                updatedDisabilityTypes,
                updatedDetailFilters,
                optionCodes,
                InfantFamily.filterCodes,
                type
            )

            is ElderlyPeople -> updateOptionState(
                updatedDisabilityTypes,
                updatedDetailFilters,
                optionCodes,
                ElderlyPeople.filterCodes,
                type
            )
        }.copy(page = 0)
    }

    private fun updateOptionState(
        updatedDisabilityTypes: TreeSet<Long>,
        updatedDetailFilters: TreeSet<Long>,
        optionCodes: List<Long>,
        filterCodes: Set<Long>,
        type: DisabilityType
    ): ListOptionState {
        updatedDetailFilters.removeAll(filterCodes)
        if (optionCodes.isNotEmpty()) {
            updatedDisabilityTypes.add(type.code)
            updatedDetailFilters.addAll(optionCodes)
        } else {
            updatedDisabilityTypes.remove(type.code)
        }
        return listOptionState.value.copy(
            disabilityType = updatedDisabilityTypes,
            detailFilter = updatedDetailFilters
        )
    }

    private fun updateUiStateWithOptionCount(optionCodes: List<Long>, type: DisabilityType): List<ListSearchUIModel> {
        return _uiState.value.map { uiModel ->
            if (uiModel is CategoryModel) {
                val newOptionState = uiModel.optionState.toMutableMap()
                newOptionState[type] = optionCodes.size
                uiModel.copy(optionState = newOptionState)
            } else {
                uiModel
            }
        }
    }

    fun onSelectedTab(category: Category) {
        clearPlace()
        listOptionState.update { it.copy(category = category, page = 0) }
    }

    fun modifyCategoryModel(optionState: Map<DisabilityType, Int>) {
        _uiState.update { uiState ->
            updateCategoryModel(uiState, optionState)
        }
    }

    private fun updateCategoryModel(uiState: List<ListSearchUIModel>, optionState: Map<DisabilityType, Int>)
    : List<ListSearchUIModel> {
        return uiState.map { uiModel ->
            when (uiModel) {
                is CategoryModel -> {
                    if (optionState.isEmpty()) {
                        uiModel.copy(optionState = mutableMapOf())
                    } else {
                        val newOptionState = uiModel.optionState.toMutableMap()
                        optionState.forEach { (type, count) ->
                            newOptionState[type] = count
                        }
                        uiModel.copy(optionState = newOptionState)
                    }
                }
                else -> uiModel
            }
        }
    }

    private suspend fun loadPlaces() {
        listOptionState.collect { listOption ->
            placeRepository.getSearchPlaceResultByList(listOption.toDomainModel())
                .onSuccess { result ->
                    _uiState.update {
                        val currentUiState = it.toMutableList()
                        val sortModelIndex = currentUiState.indexOfFirst { it is SortModel }
                        currentUiState[sortModelIndex] = SortModel(result.itemSize)

                        val noPlaceModelIndex = currentUiState.indexOfFirst { it is NoPlaceModel }

                        if (result.itemSize == 0 && noPlaceModelIndex == -1) {
                            currentUiState.add(NoPlaceModel())
                            currentUiState
                        } else {
                            if (noPlaceModelIndex != -1) currentUiState.removeAt(noPlaceModelIndex)
                            val newPlaceModels = result.toUiModel()
                            currentUiState.addAll(newPlaceModels)
                            currentUiState
                        }
                    }
                    if (result.isLastPage) _isLastPage.update { true }
                }
                .onError { e ->
                    networkErrorDelegate.handleNetworkError(e)
                }
        }
    }

    private suspend fun reloadPlace() = viewModelScope.launch {
        clearPlace()
        listOptionState.take(1).collect { listOption ->
            placeRepository.getSearchPlaceResultByList(listOption.toDomainModel())
                .onSuccess { result ->
                    _uiState.update { uiState ->
                        val currentUiState = uiState.toMutableList()
                        val newPlaceModels = result.toUiModel()
                        currentUiState.addAll(newPlaceModels)
                        currentUiState
                    }
                    if (result.isLastPage) _isLastPage.value = true
                }
                .onError { e ->
                    networkErrorDelegate.handleNetworkError(e)
                }
        }
    }

    private fun clearPlace() {
        _uiState.update { uiState -> uiState.filterNot { it is PlaceModel } }
    }

    fun onSelectedArea(areaName: String){
        clearPlace()
        val areaCode = areaCode.value.findAreaCode(areaName) ?: ""
        listOptionState.update { it.copy(areaCode = areaCode) }
        updateSigunguModel(areaCode)
    }

    private fun updateSigunguModel(areaCode: String) = viewModelScope.launch(Dispatchers.IO){
        val sigunguList = sigunguCodeRepository.getAllSigunguCode(areaCode)
        sigunguCode.update { sigunguList }

        _uiState.update { uiState ->
            val hasSigunguModel = uiState.any { it is SigunguModel }
            if (hasSigunguModel) {
                uiState.map { uiModel ->
                    if (uiModel is SigunguModel) {
                        uiModel.copy(
                            sigungus = sigunguList.getSigunguName(),
                            selectedSigungu = "시/군/구"
                        )
                    } else {
                        uiModel
                    }
                }
            } else {
                val newUiState = uiState.toMutableList()
                val sortModelIndex = newUiState.indexOfFirst { it is SortModel }
                val insertIndex = if (sortModelIndex > 0) sortModelIndex else 0
                newUiState.add(insertIndex, SigunguModel(sigunguList.getSigunguName(), "시/군/구"))
                newUiState
            }
        }
    }

    fun onSelectedSigungu(sigunguName: String) {
        val sigunguCode = sigunguCode.value.findSigunguCode(sigunguName)
        listOptionState.update { it.copy(sigunguCode = sigunguCode, page = 0) }

        _uiState.update { uiState ->
            uiState.map { uiModel ->
                if (uiModel is SigunguModel) {
                    uiModel.copy(selectedSigungu = sigunguName)
                } else {
                    uiModel
                }
            }
        }
    }

    fun whenLastPageReached() {
        listOptionState.update { it.copy(page = it.page + 1) }
    }

    private suspend fun loadAreaCodes() {
        val areaInfoList = areaCodeRepository.getAllAreaCodes()
        areaCode.update { areaInfoList }
        _uiState.update { uiStateList ->
            uiStateList.map { uiModel ->
                if (uiModel is AreaModel) {
                    uiModel.copy(areaInfoList.getAllAreaName())
                } else {
                    uiModel
                }
            }
        }
    }

    fun onMapChanged(state: Boolean) = viewModelScope.launch {
        mapChanged.emit(state)
    }

    fun onChangeMapState(state: SharedOptionState) {
        listOptionState.update {
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