package kr.techit.lion.data.dto.response.searchplace.list

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val pageNo: Int,
    val pageSize: Int,
    val placeResList: List<PlaceRes>,
    val totalSize: Int
)