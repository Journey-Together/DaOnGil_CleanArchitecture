package kr.tekit.lion.daongil.data.dto.remote.response.areacode

import com.squareup.moshi.JsonClass
import kr.tekit.lion.data.dto.remote.response.areacode.Items

@JsonClass(generateAdapter = true)
data class Body(
    val items: Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)