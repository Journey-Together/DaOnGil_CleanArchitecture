package kr.techit.lion.data.dto.response.searchplace.map

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val placeResList: List<PlaceRes>
)