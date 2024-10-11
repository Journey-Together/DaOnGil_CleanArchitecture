package kr.techit.lion.domain.model.schedule

data class MyUpcomingSchedules(
    val myUpcomingScheduleList: List<MyUpcomingScheduleInfo>,
    val last: Boolean,
)
