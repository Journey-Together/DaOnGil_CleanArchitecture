package kr.tekit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Center(
    val crs: String?,
    val x: Double?,
    val y: Double?
)
