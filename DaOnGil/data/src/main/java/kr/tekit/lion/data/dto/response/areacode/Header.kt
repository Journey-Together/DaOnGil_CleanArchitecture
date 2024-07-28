package kr.tekit.lion.data.dto.response.areacode

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Header(
    val resultCode: String,
    val resultMsg: String
)