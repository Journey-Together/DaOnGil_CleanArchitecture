package kr.techit.lion.presentation.scheduleform.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.BookmarkedPlace
import kr.techit.lion.domain.model.scheduleform.DailyPlace
import kr.techit.lion.domain.model.scheduleform.DailySchedule
import kr.techit.lion.domain.model.scheduleform.FormPlace
import kr.techit.lion.domain.model.scheduleform.NewPlan
import kr.techit.lion.domain.model.scheduleform.PlaceSearchInfoList
import kr.techit.lion.domain.model.scheduleform.PlaceSearchResult
import kr.techit.lion.domain.repository.BookmarkRepository
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.addDays
import kr.techit.lion.presentation.ext.calculateDaysUntilEndDate
import kr.techit.lion.presentation.ext.convertPeriodToDate
import kr.techit.lion.presentation.ext.formatDateValue
import kr.techit.lion.presentation.scheduleform.FormDateFormat
import kr.techit.lion.presentation.scheduleform.FormDateFormat.YYYY_MM_DD
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleFormViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {
    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    val networkState: StateFlow<NetworkState> get() = networkErrorDelegate.networkState

    private val _startDate = MutableLiveData<Date?>()
    val startDate: LiveData<Date?> get() = _startDate

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> get() = _endDate

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> get() = _title

    private val _schedule = MutableLiveData<List<DailySchedule>?>()
    val schedule: LiveData<List<DailySchedule>?> get() = _schedule

    private val _bookmarkedPlaces = MutableLiveData<List<BookmarkedPlace>>()
    val bookmarkedPlaces: LiveData<List<BookmarkedPlace>> get() = _bookmarkedPlaces

    // 여행지 검색 화면 - 검색 결과 목록 + pageNo(0~), pageSize(itemSize), totalPages, last (t/f)
    private val _placeSearchResult = MutableLiveData<PlaceSearchResult>()

    private val _keyword = MutableLiveData<String>()

    // 검색 결과 수와 장소 목록을 하나의 List로 관리
    private val _searchResultsWithNum = MediatorLiveData<List<PlaceSearchInfoList>>().apply {
        // _placeSearchResult: 이 값이 변경될 때마다 값 update
        addSource(_placeSearchResult) {
            val combinedList = mutableListOf<PlaceSearchInfoList>()
            combinedList.add(it.totalElements)
            combinedList.addAll(it.placeInfoList)
            value = combinedList
        }
    }
    val searchResultsWithNum: LiveData<List<PlaceSearchInfoList>> get() = _searchResultsWithNum

    fun setStartDate(startDate: Date?) {
        _startDate.value = startDate
    }

    fun setEndDate(endDate: Date?) {
        _endDate.value = endDate
    }

    fun setTitle(title: String?) {
        _title.value = title
    }

    fun hasDates(): Boolean {
        return (startDate.value != null) && (endDate.value != null)
    }

    fun getScheduleTitle(): String {
        return _title.value ?: ""
    }

    fun setKeyword(keyword: String) {
        _keyword.value = keyword
    }

    fun getSchedulePeriod(pattern: String): String {
        val startDateString = _startDate.value?.formatDateValue(pattern)
        val endDateString = _endDate.value?.formatDateValue(pattern)

        return "$startDateString - $endDateString"
    }

    fun getSchedulePeriodAccessibilityText(): String {
        val startDate = _startDate.value ?: Date()
        val endDate = _endDate.value ?: Date()

        return startDate.convertPeriodToDate(endDate)
    }

    private fun isScheduleEmpty(): Boolean {
        return schedule.value.isNullOrEmpty()
    }

    fun initScheduleList() {
        val startDate = _startDate.value
        val endDate = _endDate.value

        if (startDate != null && endDate != null) {
            if (!isScheduleEmpty()) {
                updateScheduleList(startDate, endDate)
                return
            }

            val createdSchedule = createScheduleList(startDate, endDate)
            _schedule.value = createdSchedule.toList()
        }
    }

    private fun createScheduleList(startDate: Date, endDate: Date): MutableList<DailySchedule> {
        val days = startDate.calculateDaysUntilEndDate(endDate)

        val schedule = mutableListOf<DailySchedule>()
        for (day in 0..days) {
            val dateInfo = startDate.addDays(day, FormDateFormat.M_D_E)
            // 0일차가 아닌 1일차부터 표기하기 위해 day+1
            schedule.add(DailySchedule(day + 1, dateInfo, mutableListOf<FormPlace>()))
        }
        return schedule
    }

    private fun updateScheduleList(startDate: Date, endDate: Date) {
        // 선택한 시작일, 종료일이 변하지 않은 경우
        if (isSchedulePeriodUnchanged(startDate, endDate)) {
            return
        }

        val updatedSchedule = createScheduleList(startDate, endDate)
        val currentSchedule = _schedule.value ?: emptyList()

        val currentSize = _schedule.value?.size ?: 0
        val newSize = updatedSchedule.size

        // 기존에 추가한 여행지 목록을 새 일정에 복제
        for (index in 0 until minOf(currentSize, newSize)) {
            val existingPlaces = currentSchedule[index].dailyPlaces
            if (existingPlaces.isEmpty()) continue
            updatedSchedule[index] = updatedSchedule[index].copy(
                dailyPlaces = (existingPlaces)
            )
        }

        // 일정이 짧아진 경우, 마지막 날에 남은 여행지들을 추가해준다.
        if (newSize < currentSize) {
            for (i in newSize until currentSize) {
                val existingPlaces = currentSchedule[i].dailyPlaces
                if (existingPlaces.isEmpty()) continue
                updatedSchedule[newSize - 1] = updatedSchedule[newSize - 1].copy(
                    dailyPlaces = (updatedSchedule[newSize - 1].dailyPlaces + existingPlaces).distinct()
                )
            }
        }

        _schedule.value = updatedSchedule.toList()
    }

    private fun isSchedulePeriodUnchanged(startDate: Date, endDate: Date): Boolean {
        val startDateStr = startDate.formatDateValue(FormDateFormat.M_D_E)
        val endDateStr = endDate.formatDateValue(FormDateFormat.M_D_E)

        val scheduleSize = _schedule.value?.size ?: 0
        val savedStartDate = _schedule.value?.get(0)?.dailyDate
        val savedEndDate = _schedule.value?.get(scheduleSize - 1)?.dailyDate

        return (startDateStr == savedStartDate) && (endDateStr == savedEndDate)
    }

    fun getPlaceSearchResult(isNewRequest: Boolean) {
        val page = if (isNewRequest) -1 else _placeSearchResult.value?.pageNo ?: -1

        val keyword = _keyword.value

        if (keyword != null) {
            viewModelScope.launch {
                networkErrorDelegate.handleNetworkLoading()

                planRepository.getPlaceSearchResult(keyword, page + 1)
                    .onSuccess {
                        if (isNewRequest) {
                            _placeSearchResult.value = it
                        } else {
                            val newList =
                                _placeSearchResult.value?.placeInfoList.orEmpty() + it.placeInfoList
                            val updatedResult = it.copy(placeInfoList = newList)
                            _placeSearchResult.value = updatedResult
                        }

                        networkErrorDelegate.handleNetworkSuccess()
                    }.onError {
                        networkErrorDelegate.handleNetworkError(it)
                    }
            }
        }
    }

    fun isLastPage(): Boolean {
        return _placeSearchResult.value?.last ?: true
    }

    private fun addNewPlace(newPlace: FormPlace, dayPosition: Int) {
        // 업데이트 될 기존 데이터
        val updatedSchedule = _schedule.value?.toMutableList()
        val daySchedule = updatedSchedule?.get(dayPosition)

        if (daySchedule != null) {
            // 업데이트할 날짜의 장소 목록
            val updatedPlaces = daySchedule.dailyPlaces.toMutableList()
            // 검색 결과에서 선택한 장소 정보를 추가
            updatedPlaces.add(newPlace)
            // copy = 일부 속성만 변경할 수 있게 해준다.
            // 여기선 dailyPlaces 속성만 updatedPlaces 로 바꿔준다.
            updatedSchedule[dayPosition] = daySchedule.copy(dailyPlaces = updatedPlaces)
            // 데이터 갱신
            _schedule.value = updatedSchedule
        }
    }

    fun removePlace(dayPosition: Int, placePosition: Int) {
        // 하나의 장소를 삭제할 예정인 기존 데이터
        val removedSchedule = _schedule.value?.toMutableList()
        val daySchedule = removedSchedule?.get(dayPosition)

        if (daySchedule != null) {
            // 하나의 장소를 삭제할 날짜의 장소 목록
            val removedPlaces = daySchedule.dailyPlaces.toMutableList()
            // 선택된 장소 정보를 List에서 제거
            removedPlaces.removeAt(placePosition)
            // 수정된 데이터를 반영해준다.
            removedSchedule[dayPosition] = daySchedule.copy(dailyPlaces = removedPlaces)
            // 데이터 갱신
            _schedule.value = removedSchedule
        }
    }

    fun getPlaceId(selectedPlacePosition: Int): Long {
        val placeId =
            _placeSearchResult.value?.placeInfoList?.get(selectedPlacePosition)?.placeId ?: -1L

        return placeId
    }

    fun isPlaceAlreadyAdded(
        dayPosition: Int,
        selectedPlacePosition: Int,
        isBookmarkedPlace: Boolean
    ): Boolean {
        val placeId = if (isBookmarkedPlace) {
            _bookmarkedPlaces.value?.get(selectedPlacePosition)?.bookmarkedPlaceId
        } else {
            _placeSearchResult.value?.placeInfoList?.get(selectedPlacePosition)?.placeId
        }

        // 선택한 관광지 정보가 같은 날에 추가된 경우
        val daySchedule = _schedule.value?.get(dayPosition)?.dailyPlaces
        daySchedule?.forEach {
            if (it.placeId == placeId) {
                return true
            }
        }

        getPlaceInfoAndSave(dayPosition, selectedPlacePosition, isBookmarkedPlace)

        return false
    }

    private fun getPlaceInfoAndSave(
        dayPosition: Int,
        selectedPlacePosition: Int,
        isBookmarkedPlace: Boolean
    ) {
        if (isBookmarkedPlace) {
            getBookmarkedPlaceDetailInfo(dayPosition, selectedPlacePosition)
        } else {
            val placeInfo = _placeSearchResult.value?.placeInfoList?.get(selectedPlacePosition)
            if (placeInfo != null) {
                val formPlace = FormPlace(
                    placeInfo.placeId,
                    placeInfo.imageUrl,
                    placeInfo.placeName,
                    placeInfo.category
                )
                addNewPlace(formPlace, dayPosition)
            }
        }
    }

    fun initBookmarkList(){
        val bookmark = _bookmarkedPlaces.value
        if(bookmark == null) {
            getBookmarkedPlaceList()
        }
    }

    private fun getBookmarkedPlaceList() {
        viewModelScope.launch {
            networkErrorDelegate.handleNetworkLoading()
            bookmarkRepository.getPlaceBookmarkList().onSuccess {
                _bookmarkedPlaces.postValue(it)

                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }

    private fun getBookmarkedPlaceDetailInfo(
        dayPosition: Int,
        selectedPlacePosition: Int
    ) {
        val placeId = _bookmarkedPlaces.value?.get(selectedPlacePosition)?.bookmarkedPlaceId

        viewModelScope.launch {
            placeId?.let {
                placeRepository.getPlaceDetailInfo(placeId).onSuccess {
                    val formPlace =
                        FormPlace(it.placeId, it.image, it.name, it.category)
                    addNewPlace(formPlace, dayPosition)
                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
            }
        }
    }

    fun resetNetworkState(){
        networkErrorDelegate.handleNetworkSuccess()
    }

    fun submitNewPlan(callback: (Boolean, Boolean) -> Unit) {
        val title = _title.value
        val startDateString = _startDate.value?.formatDateValue(YYYY_MM_DD)
        val endDateString = _endDate.value?.formatDateValue(YYYY_MM_DD)
        val dailyPlace = getDailyPlaceList()

        if (title != null && startDateString != null && endDateString != null) {
            val newPlan = NewPlan(title, startDateString, endDateString, dailyPlace)

            var requestFlag = false

            viewModelScope.launch {
                networkErrorDelegate.handleNetworkLoading()

                val success = try {
                    planRepository.addNewPlan(newPlan).onSuccess {
                        requestFlag = true

                        networkErrorDelegate.handleNetworkSuccess()

                    }.onError {
                        networkErrorDelegate.handleNetworkError(it)
                    }
                    true
                } catch (e: Exception) {
                    Log.d("submitNewPlan", "Error: ${e.message}")
                    false
                }
                // 작업한 결과가 끝나면 success에 true를 반환해준다.
                callback(success, requestFlag)
            }
        }
    }


    private fun getDailyPlaceList(): List<DailyPlace> {
        val dailyPlaceList = mutableListOf<DailyPlace>()
        val schedule = _schedule.value
        val startDate = _startDate.value

        startDate?.let {
            schedule?.forEachIndexed { index, dailySchedule ->
                val date = startDate.addDays(index, YYYY_MM_DD)
                val places = mutableListOf<Long>()
                dailySchedule.dailyPlaces.forEach {
                    places.add(it.placeId)
                }
                dailyPlaceList.add(DailyPlace(date, places))
            }
        }

        return dailyPlaceList.toList()
    }
}