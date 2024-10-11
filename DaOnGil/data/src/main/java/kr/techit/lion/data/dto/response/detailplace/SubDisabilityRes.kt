package kr.techit.lion.data.dto.response.detailplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SubDisabilityRes(
    val description: String?,
    val subDisabilityName: String
)