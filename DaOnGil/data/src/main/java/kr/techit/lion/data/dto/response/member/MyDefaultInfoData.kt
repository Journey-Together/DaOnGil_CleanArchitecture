package kr.techit.lion.daongil.data.dto.remote.response.member

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class MyDefaultInfoData(
    val date: Int,
    val name: String,
    val profileImg: String,
    val reviewNum: Int
)