package kr.tekit.lion.data.dto.response.emergency.realtime

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Header(
    val resultCode: String,
    val resultMsg: String
)
