package kr.techit.lion.presentation.schedule.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.OpenPlanInfo
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class PublicScheduleViewModel @Inject constructor(
    private val planRepository: PlanRepository
): ViewModel() {

    private val _openPlanList = MutableLiveData<List<OpenPlanInfo>>()
    val openPlanList : LiveData<List<OpenPlanInfo>> = _openPlanList

    private val _isLastPage = MutableLiveData<Boolean>()
    val isLastPage: LiveData<Boolean> = _isLastPage

    private val _pageNo = MutableLiveData<Int>()
    val pageNo: LiveData<Int> = _pageNo

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState get() = networkErrorDelegate.networkState

    init {
        getOpenPlanList()
    }

    fun getOpenPlanList() = viewModelScope.launch {
        planRepository.getOpenPlanList(10, 0).onSuccess {
            _openPlanList.value = it.openPlanList
            _isLastPage.value = it.last
            _pageNo.value = it.pageNo + 1
            networkErrorDelegate.handleNetworkSuccess()
        }.onError {
            networkErrorDelegate.handleNetworkError(it)
        }
    }

    fun getOpenPlanListPaging() = viewModelScope.launch {
        if (isLastPage.value == false) {
            pageNo.value?.let { pageNo ->
                planRepository.getOpenPlanList(10, pageNo).onSuccess {
                    val currentList = _openPlanList.value.orEmpty()
                    _openPlanList.value = currentList + it.openPlanList
                    _isLastPage.value = it.last
                    _pageNo.value = it.pageNo + 1
                    networkErrorDelegate.handleNetworkSuccess()
                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
            }
        }
    }
}