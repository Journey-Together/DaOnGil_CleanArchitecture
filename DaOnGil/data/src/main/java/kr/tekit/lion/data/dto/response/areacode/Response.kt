package kr.tekit.lion.data.dto.response.areacode

import com.squareup.moshi.JsonClass
import kr.tekit.lion.data.dto.response.areacode.Body
import kr.tekit.lion.data.dto.response.areacode.Header

@JsonClass(generateAdapter = true)
internal data class Response(
    val body: Body,
    val header: Header
)