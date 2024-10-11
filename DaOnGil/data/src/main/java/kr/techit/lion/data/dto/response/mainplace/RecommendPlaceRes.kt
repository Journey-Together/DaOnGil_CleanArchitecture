package kr.techit.lion.data.dto.response.mainplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RecommendPlaceRes(
    val address: String,
    val disability: List<String>,
    val image: String,
    val name: String,
    val placeId: Long
)