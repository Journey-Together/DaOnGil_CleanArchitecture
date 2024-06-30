package kr.tekit.lion.data.dto.remote.response.areacode

import com.squareup.moshi.JsonClass
import kr.tekit.lion.daongil.data.dto.remote.response.areacode.Body

@JsonClass(generateAdapter = true)
data class Response(
    val body: Body,
    val header: Header
)