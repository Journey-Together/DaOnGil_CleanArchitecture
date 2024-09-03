package kr.tekit.lion.presentation.scheduleform.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.tekit.lion.domain.model.scheduleform.DailySchedule
import kr.tekit.lion.domain.model.scheduleform.FormPlace
import kr.tekit.lion.presentation.ext.addDays
import kr.tekit.lion.presentation.ext.calculateDaysUntilEndDate
import kr.tekit.lion.presentation.ext.formatDateValue
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleFormViewModel @Inject constructor(

): ViewModel() {
    private val _startDate = MutableLiveData<Date?>()
    val startDate: LiveData<Date?> get() = _startDate

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> get() = _endDate

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> get() = _title

    private val _schedule = MutableLiveData<List<DailySchedule>?>()
    val schedule : LiveData<List<DailySchedule>?> get() = _schedule
    //

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

    private fun updateScheduleList(startDate: Date, endDate: Date){
        // 선택한 시작일, 종료일이 변하지 않은 경우
        if(isSchedulePeriodUnchanged(startDate, endDate)){
            return
        }

        val updatedSchedule = createScheduleList(startDate, endDate)
        val currentSchedule = _schedule.value ?: emptyList()

        val currentSize = _schedule.value?.size ?: 0
        val newSize = updatedSchedule.size

        // 새로운 일정이 더 길 때
        if(newSize > currentSize){
            currentSchedule.forEachIndexed { index, dailySchedule ->
                val existingPlaces = dailySchedule.dailyPlaces
                updatedSchedule[index].dailyPlaces.toMutableList().addAll(existingPlaces)
            }
        }

        if (newSize <= currentSize) {
            updatedSchedule.forEachIndexed { index, dailySchedule ->
                val existingPlaces = currentSchedule[index].dailyPlaces
                updatedSchedule[newSize-1].dailyPlaces.toMutableList().addAll(existingPlaces)
            }

            // 새로운 일정이 더 짧을 때
            if(newSize < currentSize){
                for (i in newSize until currentSize) {
                    val existingPlaces = currentSchedule[i].dailyPlaces
                    updatedSchedule[newSize-1].dailyPlaces.toMutableList().addAll(existingPlaces)
                }
            }
        }
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




}