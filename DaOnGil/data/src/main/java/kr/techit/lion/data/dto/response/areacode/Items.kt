package kr.techit.lion.data.dto.response.areacode

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Items(
    val item: List<Item>
)