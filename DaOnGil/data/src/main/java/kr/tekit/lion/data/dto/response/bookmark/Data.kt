package kr.tekit.lion.data.dto.response.bookmark


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val state: Boolean
)