package kr.techit.lion.presentation.myschedule.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.schedule.MyElapsedScheduleInfo
import kr.techit.lion.domain.model.schedule.MyUpcomingScheduleInfo
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import javax.inject.Inject

private const val INITIAL_PAGE_NO = 0

@HiltViewModel
class MyScheduleViewModel @Inject constructor(
    private val planRepository: PlanRepository
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate
    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _upcomingSchedules = MutableLiveData<List<MyUpcomingScheduleInfo>?>()
    val upcomingSchedules: LiveData<List<MyUpcomingScheduleInfo>?> get() = _upcomingSchedules

    private val _upcomingPageNo = MutableLiveData<Int>()
    private val _isLastUpcoming = MutableLiveData<Boolean>()

    private val _elapsedSchedules = MutableLiveData<List<MyElapsedScheduleInfo>?>()
    val elapsedSchedules: LiveData<List<MyElapsedScheduleInfo>?> get() = _elapsedSchedules

    private val _elapsedPageNo = MutableLiveData<Int>()
    private val _isLastElapsed = MutableLiveData<Boolean>()

    init {
        refreshScheduleList()
    }

    private fun setUpcomingPageNo(pageNum: Int) {
        _upcomingPageNo.value = pageNum
    }

    private fun setElapsedPageNo(pageNum: Int) {
        _elapsedPageNo.value = pageNum
    }

    private fun getMyUpcomingScheduleList(page: Int) {
        setUpcomingPageNo(page)
        viewModelScope.launch {

            // networkState =  Loading
            if (page != INITIAL_PAGE_NO) {
                networkErrorDelegate.handleNetworkLoading()
            }

            planRepository.getMyUpcomingScheduleList(page)
                .onSuccess {
                    val newList = if (page == INITIAL_PAGE_NO) {
                        it.myUpcomingScheduleList
                    } else {
                        _upcomingSchedules.value.orEmpty() + it.myUpcomingScheduleList
                    }
                    _upcomingSchedules.value = newList
                    _isLastUpcoming.value = it.last

                    // networkState = Success
                    networkErrorDelegate.handleNetworkSuccess()

                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
        }
    }

    private fun getMyElapsedScheduleList(page: Int) {
        setElapsedPageNo(page)

        // networkState =  Loading
        if (page != INITIAL_PAGE_NO) {
            networkErrorDelegate.handleNetworkLoading()
        }

        viewModelScope.launch {
            planRepository.getMyElapsedScheduleList(page)
                .onSuccess {
                    val newList = if (page == INITIAL_PAGE_NO) {
                        it.myElapsedScheduleList
                    } else {
                        _elapsedSchedules.value.orEmpty() + it.myElapsedScheduleList
                    }
                    _elapsedSchedules.value = newList
                    _isLastElapsed.value = it.last

                    // networkState = Success
                    networkErrorDelegate.handleNetworkSuccess()
                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
        }
    }

    fun getUpcomingPlanId(planPosition: Int): Long {
        return _upcomingSchedules.value?.get(planPosition)?.planId ?: -1
    }

    fun getElapsedPlanId(planPosition: Int): Long {
        return _elapsedSchedules.value?.get(planPosition)?.planId ?: -1
    }

    fun isUpcomingLastPage(): Boolean {
        return _isLastUpcoming.value ?: true
    }

    fun isElapsedLastPage(): Boolean {
        return _isLastElapsed.value ?: true
    }

    fun fetchNextUpcomingSchedules() {
        val pageNo = _upcomingPageNo.value

        pageNo?.let {
            getMyUpcomingScheduleList(it + 1)
        } ?: run {
            getMyUpcomingScheduleList(INITIAL_PAGE_NO)
        }
    }

    fun fetchNextElapsedSchedules() {
        val pageNo = _elapsedPageNo.value

        pageNo?.let {
            getMyElapsedScheduleList(it + 1)
        } ?: run {
            getMyElapsedScheduleList(INITIAL_PAGE_NO)
        }
    }

    fun refreshScheduleList() {
        getMyUpcomingScheduleList(INITIAL_PAGE_NO)
        getMyElapsedScheduleList(INITIAL_PAGE_NO)
    }

    fun isUpcomingScheduleListEmpty(): Boolean {
        return _upcomingSchedules.value?.isEmpty() ?: false
    }

    fun isElapsedScheduleListEmpty(): Boolean {
        return _elapsedSchedules.value?.isEmpty() ?: false
    }
}