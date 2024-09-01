package kr.tekit.lion.data.dto.response.emergency.realtime

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response(
    val body: Body,
    val header: Header
)
