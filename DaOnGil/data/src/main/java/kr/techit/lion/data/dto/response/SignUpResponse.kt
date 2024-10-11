package kr.techit.lion.data.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String,
)