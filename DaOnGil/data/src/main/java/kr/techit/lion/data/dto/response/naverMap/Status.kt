package kr.techit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Status(
    val code: Int?,
    val message: String?,
    val name: String?
)
