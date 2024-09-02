package kr.tekit.lion.domain.model.schedule

data class MyElapsedSchedules(
    val myElapsedScheduleList: List<MyElapsedScheduleInfo>,
    val last: Boolean,
)
