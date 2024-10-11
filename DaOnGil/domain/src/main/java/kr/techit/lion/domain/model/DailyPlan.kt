package kr.techit.lion.domain.model

data class DailyPlan(
    val dailyPlanDate: String,
    val schedulePlaces : List<SchedulePlace>
)
