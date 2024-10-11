package kr.techit.lion.daongil.data.dto.remote.response.bookmark

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlanBookmarkData(
    val image: String?,
    val name: String,
    val planId: Long,
    val profileImg: String,
    val title: String
)