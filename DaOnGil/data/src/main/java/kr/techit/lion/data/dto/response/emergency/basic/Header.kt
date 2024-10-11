package kr.techit.lion.data.dto.response.emergency.basic

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Header(
    val resultCode: String,
    val resultMsg: String
)
