package kr.tekit.lion.daongil.data.dto.remote.response.signin


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    val email: String,
    val loginType: String,
    val memberId: Int,
    val memberType: String,
    val name: String,
    val profileUuid: String,
    val refreshToken: String,
    val accessToken: String,
)