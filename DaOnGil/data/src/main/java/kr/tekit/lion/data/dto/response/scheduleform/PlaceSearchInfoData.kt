package kr.tekit.lion.data.dto.response.scheduleform

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaceSearchInfoData(
    val placeId: Long,
    val placeName: String,
    val category: String,
    val imageUrl: String?,
)
