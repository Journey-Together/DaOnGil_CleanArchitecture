package kr.techit.lion.data.dto.response.areacode

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Item(
    @Json(name = "rnum")
    val rNum: Int,
    val code: String,
    val name: String
)