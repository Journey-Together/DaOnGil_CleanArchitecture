package kr.tekit.lion.data.dto.response.areacode

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Body(
    val items: Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)