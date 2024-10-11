package kr.techit.lion.domain.model.scheduleform

data class DailyPlace(
    val date: String,
    val places: List<Long?>,
)