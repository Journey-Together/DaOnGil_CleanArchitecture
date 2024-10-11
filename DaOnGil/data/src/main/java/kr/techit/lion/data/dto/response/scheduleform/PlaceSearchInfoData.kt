package kr.techit.lion.data.dto.response.scheduleform

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PlaceSearchInfoData(
    val placeId: Long,
    val placeName: String,
    val category: String,
    val imageUrl: String?,
)
