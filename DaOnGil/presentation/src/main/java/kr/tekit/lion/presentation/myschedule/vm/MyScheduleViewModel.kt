package kr.tekit.lion.presentation.myschedule.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.schedule.MyElapsedScheduleInfo
import kr.tekit.lion.domain.model.schedule.MyUpcomingScheduleInfo
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class MyScheduleViewModel @Inject constructor(
    private val planRepository: PlanRepository
) : ViewModel() {


    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _upcomingSchedules = MutableLiveData<List<MyUpcomingScheduleInfo>?>()
    val upcomingSchedules: LiveData<List<MyUpcomingScheduleInfo>?> get() = _upcomingSchedules

    private val _upcomingPageNo = MutableLiveData<Int>()
    private val _isLastUpcoming = MutableLiveData<Boolean>()

    private val _elapsedSchedules = MutableLiveData<List<MyElapsedScheduleInfo>?>()
    val elapsedSchedules: LiveData<List<MyElapsedScheduleInfo>?> get() = _elapsedSchedules

    private val _elapsedPageNo = MutableLiveData<Int>()
    private val _isLastElapsed = MutableLiveData<Boolean>()

    init {
        getMyUpcomingScheduleList(0)
        getMyElapsedScheduleList(0)
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
            planRepository.getMyUpcomingScheduleList(page)
                .onSuccess {
                    _upcomingSchedules.value = it.myUpcomingScheduleList
                    _isLastUpcoming.value = it.last
                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
        }
    }

    private fun getMyElapsedScheduleList(page: Int) {
        setElapsedPageNo(page)
        viewModelScope.launch {
            planRepository.getMyElapsedScheduleList(page)
                .onSuccess {
                    _elapsedSchedules.value = it.myElapsedScheduleList
                    _isLastElapsed.value = it.last
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
        val page = _upcomingPageNo.value

        if (page != null) {
            setUpcomingPageNo(page + 1) // 페이지 번호 갱신

            viewModelScope.launch {
                planRepository.getMyUpcomingScheduleList(page + 1)
                    .onSuccess {
                        val newList = _upcomingSchedules.value.orEmpty() + it.myUpcomingScheduleList
                        _upcomingSchedules.value = newList
                        _isLastUpcoming.value = it.last
                    }.onError {
                        networkErrorDelegate.handleNetworkError(it)
                    }
            }
        }
    }

    fun fetchNextElapsedSchedules() {
        val page = _elapsedPageNo.value

        if (page != null) {
            setElapsedPageNo(page + 1)

            viewModelScope.launch {
                planRepository.getMyElapsedScheduleList(page + 1)
                    .onSuccess {
                        val newList = _elapsedSchedules.value.orEmpty() + it.myElapsedScheduleList
                        _elapsedSchedules.value = newList
                        _isLastElapsed.value = it.last
                    }.onError {
                        networkErrorDelegate.handleNetworkError(it)
                    }
            }
        }
    }
}