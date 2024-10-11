package kr.techit.lion.data.dto.response.bookmark


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val state: Boolean
)