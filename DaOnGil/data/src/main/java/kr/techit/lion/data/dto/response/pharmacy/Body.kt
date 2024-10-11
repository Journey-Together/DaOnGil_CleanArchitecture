package kr.techit.lion.data.dto.response.pharmacy

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Body(
    val items: Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)