package kr.techit.lion.data.dto.response.signin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SignInResponse(
    val code: Int = 0,
    val data: Data,
    val message: String
)