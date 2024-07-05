package kr.tekit.lion.daongil.data.dto.remote.response.signin


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignInResponse(
    val code: Int = 0,
    val data: Data,
    val message: String
)