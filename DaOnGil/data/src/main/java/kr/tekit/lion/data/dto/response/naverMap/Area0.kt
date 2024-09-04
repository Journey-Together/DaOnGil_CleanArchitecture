package kr.tekit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Area0(
    val coords: Coords?,
    val name: String?
)
