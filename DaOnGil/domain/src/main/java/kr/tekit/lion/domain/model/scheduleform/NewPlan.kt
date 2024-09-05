package kr.tekit.lion.domain.model.scheduleform

data class NewPlan(
    val title: String,
    val startDate: String,
    val endDate: String,
    val dailyPlace: List<DailyPlace>,
)
