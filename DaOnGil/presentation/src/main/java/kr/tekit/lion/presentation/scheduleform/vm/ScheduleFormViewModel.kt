package kr.tekit.lion.presentation.scheduleform.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.BookmarkedPlace
import kr.tekit.lion.domain.model.scheduleform.DailySchedule
import kr.tekit.lion.domain.model.scheduleform.FormPlace
import kr.tekit.lion.domain.model.scheduleform.PlaceSearchInfoList
import kr.tekit.lion.domain.model.scheduleform.PlaceSearchResult
import kr.tekit.lion.domain.repository.BookmarkRepository
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.ext.addDays
import kr.tekit.lion.presentation.ext.calculateDaysUntilEndDate
import kr.tekit.lion.presentation.ext.formatDateValue
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleFormViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val bookmarkRepository: BookmarkRepository,
): ViewModel() {
    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _startDate = MutableLiveData<Date?>()
    val startDate: LiveData<Date?> get() = _startDate

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> get() = _endDate

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> get() = _title

    private val _schedule = MutableLiveData<List<DailySchedule>?>()
    val schedule : LiveData<List<DailySchedule>?> get() = _schedule

    private val _bookmarkedPlaces = MutableLiveData<List<BookmarkedPlace>>()
    val bookmarkedPlaces: LiveData<List<BookmarkedPlace>> get() = _bookmarkedPlaces

    // 여행지 검색 화면 - 검색 결과 목록 + pageNo(0~), pageSize(itemSize), totalPages, last (t/f)
    private val _placeSearchResult = MutableLiveData<PlaceSearchResult>()
//    val placeSearchResult : LiveData<PlaceSearchResult> get() = _placeSearchResult

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
    val searchResultsWithNum : LiveData<List<PlaceSearchInfoList>> get() = _searchResultsWithNum

    init {
        getBookmarkedPlaceList()
    }

    fun setStartDate(startDate: Date?){
        _startDate.value = startDate
    }

    fun setEndDate(endDate : Date?){
        _endDate.value = endDate
    }

    fun setTitle(title: String?){
        _title.value = title
    }

    fun hasDates() : Boolean {
        return (startDate.value != null) && (endDate.value != null)
    }

    fun getScheduleTitle(): String {
        return _title.value ?: ""
    }

    fun setKeyword(keyword: String){
        _keyword.value = keyword
    }

    fun getSchedulePeriod(): String {
        val pattern = "yyyy-MM-dd"
        val startDateString = _startDate.value?.formatDateValue(pattern)
        val endDateString = _endDate.value?.formatDateValue(pattern)

        return "$startDateString - $endDateString"
    }

    private fun isScheduleEmpty(): Boolean {
        return schedule.value.isNullOrEmpty()
    }

    fun initScheduleList(){
        val startDate = _startDate.value
        val endDate = _endDate.value

        if (startDate != null && endDate != null) {
            if(!isScheduleEmpty()){
                updateScheduleList(startDate, endDate)
                return
            }

            val createdSchedule = createScheduleList(startDate, endDate)
            _schedule.value = createdSchedule.toList()
        }
    }

    private fun createScheduleList(startDate: Date, endDate: Date) : MutableList<DailySchedule>{
        val days =  startDate.calculateDaysUntilEndDate(endDate)

        val schedule = mutableListOf<DailySchedule>()
        for (day in 0..days) {
            val dateInfo = startDate.addDays(day)
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
                dailyPlaces = (updatedSchedule[index].dailyPlaces + existingPlaces)
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

    private fun isSchedulePeriodUnchanged(startDate: Date, endDate: Date) : Boolean {
        val scheduleDatePattern = "M월 d일 (E)"

        val startDateStr = startDate.formatDateValue(scheduleDatePattern)
        val endDateStr = endDate.formatDateValue(scheduleDatePattern)

        val scheduleSize = _schedule.value?.size ?: 0
        val savedStartDate = _schedule.value?.get(0)?.dailyDate
        val savedEndDate = _schedule.value?.get(scheduleSize-1)?.dailyDate

        return (startDateStr==savedStartDate) && (endDateStr==savedEndDate)
    }

    fun getPlaceSearchResult(isNewRequest : Boolean) {
        val page = if (isNewRequest) -1 else _placeSearchResult.value?.pageNo ?: -1

        val keyword = _keyword.value

        if (keyword != null) {
            viewModelScope.launch {
                planRepository.getPlaceSearchResult(keyword, page + 1)
                    .onSuccess {
                        if(isNewRequest){
                            _placeSearchResult.value = it
                        }else{
                            val newList =
                                _placeSearchResult.value?.placeInfoList.orEmpty() + it.placeInfoList
                            val updatedResult = it.copy(placeInfoList = newList)
                            _placeSearchResult.value = updatedResult
                        }
                    }.onError {
                        networkErrorDelegate.handleNetworkError(it)
                    }
            }
        }
    }

    fun isLastPage(): Boolean {
        return _placeSearchResult.value?.last ?: true
    }

    private fun addNewPlace(newPlace:FormPlace, dayPosition:Int){
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

    fun removePlace(dayPosition: Int, placePosition: Int){
        // 하나의 장소를 삭제할 예정인 기존 데이터
        val removedSchedule = _schedule.value?.toMutableList()
        val daySchedule = removedSchedule?.get(dayPosition)

        if(daySchedule != null){
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
        val placeId = _placeSearchResult.value?.placeInfoList?.get(selectedPlacePosition)?.placeId ?: -1L

        return placeId
    }

    fun isPlaceAlreadyAdded(
        dayPosition: Int,
        selectedPlacePosition: Int,
        isBookmarkedPlace: Boolean
    ): Boolean {
//        val placeId = if (isBookmarkedPlace) {
//            _bookmarkedPlaces.value?.get(selectedPlacePosition)?.bookmarkedPlaceId
//        } else {
//            _placeSearchResult.value?.placeInfoList?.get(selectedPlacePosition)?.placeId
//        }
        val placeInfo = _placeSearchResult.value?.placeInfoList?.get(selectedPlacePosition)


        // 선택한 관광지 정보가 같은 날에 추가된 경우
        val daySchedule = _schedule.value?.get(dayPosition)?.dailyPlaces
        daySchedule?.forEach {
            if (it.placeId == placeInfo?.placeId) {
                return true
            }
        }
        if(placeInfo!=null){
            val formPlace = FormPlace(placeInfo.placeId, placeInfo.imageUrl, placeInfo.placeName, placeInfo.category)
            addNewPlace(formPlace, dayPosition)
        }
        return false
    }

    private fun getBookmarkedPlaceList(){
        viewModelScope.launch {
            bookmarkRepository.getPlaceBookmarkList().onSuccess {
                _bookmarkedPlaces.postValue(it)
            }.onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }
    }
}