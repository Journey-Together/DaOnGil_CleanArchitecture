package kr.tekit.lion.data.dto.request

internal data class DailyPlaceRequest(
    val date: String,
    val places: List<Long?>,
)
