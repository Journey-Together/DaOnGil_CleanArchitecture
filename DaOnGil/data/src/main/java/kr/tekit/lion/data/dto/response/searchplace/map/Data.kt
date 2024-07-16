package kr.tekit.lion.data.dto.response.searchplace.map

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    val placeResList: List<PlaceRes>
)