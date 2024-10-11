package kr.techit.lion.presentation.main.vm.search

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
import kr.techit.lion.domain.model.area.AreaCodeList
import kr.techit.lion.domain.model.area.SigunguCodeList
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.search.ListSearchResultList
import kr.techit.lion.domain.repository.AreaCodeRepository
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.domain.repository.SigunguCodeRepository
import kr.techit.lion.presentation.base.BaseViewModel
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.main.model.AreaModel
import kr.techit.lion.presentation.main.model.Category
import kr.techit.lion.presentation.main.model.CategoryModel
import kr.techit.lion.presentation.main.model.DisabilityType
import kr.techit.lion.presentation.main.model.ElderlyPeople
import kr.techit.lion.presentation.main.model.HearingImpairment
import kr.techit.lion.presentation.main.model.InfantFamily
import kr.techit.lion.presentation.main.model.ListOptionState
import kr.techit.lion.presentation.main.model.ListSearchUIModel
import kr.techit.lion.presentation.main.model.NoPlaceModel
import kr.techit.lion.presentation.main.model.PhysicalDisability
import kr.techit.lion.presentation.main.model.PlaceModel
import kr.techit.lion.presentation.main.model.SharedOptionState
import kr.techit.lion.presentation.main.model.SigunguModel
import kr.techit.lion.presentation.main.model.SortModel
import kr.techit.lion.presentation.main.model.VisualImpairment
import kr.techit.lion.presentation.main.model.toUiModel
import java.util.TreeSet
import javax.inject.Inject

@HiltViewModel
class SearchListViewModel @Inject constructor(
    private val areaCodeRepository: AreaCodeRepository,
    private val sigunguCodeRepository: SigunguCodeRepository,
    private val placeRepository: PlaceRepository,
) : BaseViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    init {
        viewModelScope.launch {
            mapChanged.collect { changed ->
                if (changed) reloadPlace()
            }
        }

        viewModelScope.launch(recordExceptionHandler) {
            loadAreaCodes()
            loadPlaces()
        }
    }

    val networkState get() = networkErrorDelegate.networkState

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

    private val listOptionState = MutableStateFlow(ListOptionState.create())

    private val areaCode = MutableStateFlow(AreaCodeList(emptyList()))

    private val sigunguCode = MutableStateFlow(SigunguCodeList(emptyList()))

    private val mapChanged get() = MutableSharedFlow<Boolean>()

    fun onSelectOption(optionCodes: List<Long>, type: DisabilityType) {
        viewModelScope.launch(recordExceptionHandler){
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
        }
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
            detailFilter = updatedDetailFilters,
            page = 0
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
        viewModelScope.launch(Dispatchers.IO) {
            clearPlace()
            listOptionState.update { it.copy(category = category, page = 0) }
        }
    }

    fun modifyCategoryModel(optionState: Map<DisabilityType, Int>) {
        viewModelScope.launch((Dispatchers.IO)) {
            _uiState.update { uiState ->
                updateCategoryModel(uiState, optionState)
            }
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

    private fun loadPlaces(){
        viewModelScope.launch(recordExceptionHandler) {
            listOptionState.collect { listOption ->
                networkErrorDelegate.handleNetworkLoading()
                placeRepository.getSearchPlaceResultByList(listOption.toDomainModel())
                    .onSuccess { result ->
                        modifyUiState(result)
                    }
                    .onError { e ->
                        networkErrorDelegate.handleNetworkError(e)
                    }
            }
        }
    }

    private fun reloadPlace() {
        viewModelScope.launch(recordExceptionHandler) {
            clearPlace()
            listOptionState.take(1).collect { listOption ->
                placeRepository.getSearchPlaceResultByList(listOption.toDomainModel())
                    .onSuccess { result ->
                        modifyUiState(result)
                    }
                    .onError { e ->
                        networkErrorDelegate.handleNetworkError(e)
                    }
            }
        }
    }

    private fun modifyUiState(newUiState: ListSearchResultList){
        _uiState.update {
            val currentUiState = it.toMutableList()
            val sortModelIndex = currentUiState.indexOfFirst { it is SortModel }
            currentUiState[sortModelIndex] = SortModel(newUiState.itemSize)

            val noPlaceModelIndex = currentUiState.indexOfFirst { it is NoPlaceModel }
            val itemSize = newUiState.itemSize

            if (itemSize == 0 && noPlaceModelIndex == -1) {
                currentUiState.add(NoPlaceModel())
                currentUiState
            } else if (itemSize > 0) {
                if (noPlaceModelIndex != -1) currentUiState.removeAt(noPlaceModelIndex)
                val newPlaceModels = newUiState.toUiModel()
                currentUiState.addAll(newPlaceModels)
                currentUiState
            }else{
                currentUiState
            }
        }
        _isLastPage.value = newUiState.isLastPage
        networkErrorDelegate.handleNetworkSuccess()
    }

    private fun clearPlace(){
        _uiState.update { uiState -> uiState.filterNot { it is PlaceModel } }
    }

    fun onSelectedArea(areaName: String) = viewModelScope.launch(Dispatchers.IO) {
        val currentAreaCode = listOptionState.value.areaCode
        val newAreaCode = areaCode.value.findAreaCode(areaName)

        if (currentAreaCode != newAreaCode) {
            clearPlace()
            listOptionState.update { it.copy(areaCode = newAreaCode, sigunguCode = null, page = 0) }
            updateSigunguModel(newAreaCode)
        }
    }

    private fun updateSigunguModel(areaCode: String) = viewModelScope.launch(Dispatchers.IO) {
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

    fun onSelectedSigungu(sigunguName: String) = viewModelScope.launch(Dispatchers.IO) {
        val currentSigunguCode = listOptionState.value.sigunguCode
        val newSigunguCode = sigunguCode.value.findSigunguCode(sigunguName)

        if (currentSigunguCode != newSigunguCode){
            clearPlace()

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
    }

    fun whenLastPageReached() {
        listOptionState.update { it.copy(page = it.page + 1) }
    }

    private suspend fun loadAreaCodes() {
        val areaInfoList = areaCodeRepository.getAllAreaCodes()
        areaCode.value = areaInfoList
        viewModelScope.launch((Dispatchers.IO)) {
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
    }

    fun onMapChanged(state: Boolean) = viewModelScope.launch {
        mapChanged.emit(state)
    }

    fun onChangeMapState(state: SharedOptionState) = viewModelScope.launch(Dispatchers.IO) {
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
}