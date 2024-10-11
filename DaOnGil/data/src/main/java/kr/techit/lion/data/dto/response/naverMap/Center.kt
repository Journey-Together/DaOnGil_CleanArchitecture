package kr.techit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Center(
    val crs: String?,
    val x: Double?,
    val y: Double?
)
