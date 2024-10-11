package kr.techit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Code(
    val id: String?,
    val mappingId: String?,
    val type: String?
)
