package kr.tekit.lion.presentation.scheduleform.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun hasStartDate() : Boolean {
        return startDate.value != null
    }

    fun getScheduleTitle(): String {
        return _title.value ?: ""
    }


}