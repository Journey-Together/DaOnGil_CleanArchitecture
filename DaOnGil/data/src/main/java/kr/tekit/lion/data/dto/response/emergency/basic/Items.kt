package kr.tekit.lion.data.dto.response.emergency.basic

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Items(
    val item: Item
)
