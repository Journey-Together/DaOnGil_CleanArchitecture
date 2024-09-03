package kr.tekit.lion.domain.model.scheduleform

data class DailySchedule(
    val dailyIdx : Int,
    val dailyDate : String,
    val dailyPlaces : List<FormPlace>
)
