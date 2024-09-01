package kr.tekit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Coords(
    val center: Center?
)
