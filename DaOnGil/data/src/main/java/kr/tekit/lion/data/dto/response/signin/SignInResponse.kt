package kr.tekit.lion.data.dto.response.signin

import com.squareup.moshi.JsonClass
import kr.tekit.lion.data.dto.response.signin.Data

@JsonClass(generateAdapter = true)
internal data class SignInResponse(
    val code: Int = 0,
    val data: Data,
    val message: String
)