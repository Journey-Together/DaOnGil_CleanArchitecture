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
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.ext.convertStringToDate
import kr.tekit.lion.presentation.ext.formatDateValue
import kr.tekit.lion.presentation.scheduleform.FormDateFormat
import kr.tekit.lion.presentation.scheduleform.model.OriginalDailyPlan
import kr.tekit.lion.presentation.scheduleform.model.OriginalScheduleInfo
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ModifyScheduleFormViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    // 수정 전 일정 정보
    private val _originalSchedule = MutableLiveData<OriginalScheduleInfo>()

    // UI에 보여지는 정보 (시작일, 종료일, 여행명, 스케쥴)
    private val _startDate = MutableLiveData<Date?>()
    val startDate: LiveData<Date?> get() = _startDate

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> get() = _endDate

    // 기간이 한 번이라도 수정되었는지 확인하는 flag
    val _isPeriodChanged = MutableLiveData<Boolean>(false)

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> get() = _title

    private val _schedule = MutableLiveData<List<DailySchedule>?>()
    val schedule : LiveData<List<DailySchedule>?> get() = _schedule

    // 북마크한 여행지 목록
    private val _bookmarkedPlaces = MutableLiveData<List<BookmarkedPlace>>()
    val bookmarkedPlaces: LiveData<List<BookmarkedPlace>> get() = _bookmarkedPlaces

    // 여행지 검색 결과 목록
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

    val searchResultsWithNum : LiveData<List<PlaceSearchInfoList>> get() = _searchResultsWithNum

    init {
        getBookmarkedPlaceList()
    }

    fun setTitle(title: String?){
        _title.value = title
    }

    fun setStartDate(startDate: Date?){
        _startDate.value = startDate
    }

    fun setEndDate(endDate : Date?){
        _endDate.value = endDate
    }

    fun hasStartDate() : Boolean {
        return startDate.value != null
    }

    fun isLastPage(): Boolean {
        return _placeSearchResult.value?.last ?: true
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

    fun initScheduleDetailInfo(schedule: OriginalScheduleInfo) {
        _originalSchedule.value = schedule
        initScheduleData()
    }

    private fun initScheduleData() {
        _originalSchedule.value?.let {
            _startDate.value = it.startDate.convertStringToDate()
            _endDate.value = it.endDate.convertStringToDate()
            _title.value = it.title
            _schedule.value = processScheduleInfoData(it.dailyPlans)
        }
    }

    private fun processScheduleInfoData(plans: List<OriginalDailyPlan>) : List<DailySchedule>{
        return plans.mapIndexed { index, dailyPlan ->
            DailySchedule(
                dailyIdx = index,
                dailyDate = formatDateValue(
                    dateString = dailyPlan.dailyPlanDate,
                    pattern = FormDateFormat.M_D_E
                ),
                dailyPlaces = dailyPlan.schedulePlaces.map { schedulePlace ->
                    FormPlace(
                        placeId = schedulePlace.placeId,
                        placeName = schedulePlace.name,
                        placeImage = schedulePlace.imageUrl,
                        placeCategory = schedulePlace.category

                    )
                }
            )
        }
    }

    /** String 형태의 날짜를 다른 형태의 String 으로 변환 */
    private fun formatDateValue(dateString: String, pattern: String) : String{
        // Date 객체로 변환한 뒤, String 으로 다시 변환
        val date = dateString.convertStringToDate()
        return date.formatDateValue(pattern)
    }

    fun formatPickedDates() : String {
        val startDateFormatted =
            _startDate.value?.formatDateValue(FormDateFormat.YYYY_MM_DD_E)
        val endDateFormatted =
            _endDate.value?.formatDateValue(FormDateFormat.YYYY_MM_DD_E)
        return "$startDateFormatted - $endDateFormatted"
    }
}