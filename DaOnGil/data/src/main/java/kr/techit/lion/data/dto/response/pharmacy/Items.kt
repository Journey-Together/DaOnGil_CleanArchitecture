package kr.techit.lion.data.dto.response.pharmacy

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Items(
    val item: List<Item>
)