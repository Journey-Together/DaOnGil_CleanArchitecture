package kr.techit.lion.data.dto.response.searchplace.map

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PlaceRes(
    val address: String,
    val disability: List<String>,
    val image: String,
    val mapX: Double,
    val mapY: Double,
    val name: String,
    val placeId: Int
)