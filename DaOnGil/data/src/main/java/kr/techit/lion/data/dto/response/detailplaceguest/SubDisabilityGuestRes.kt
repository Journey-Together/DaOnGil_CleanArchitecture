package kr.techit.lion.data.dto.response.detailplaceguest

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SubDisabilityGuestRes (
    val description: String?,
    val subDisabilityName: String
)