package kr.techit.lion.data.dto.response.plan.openSchedule

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class OpenPlanRes(
    val date: String,
    val imageUrl: String?,
    val memberId: Int,
    val memberImageUrl: String,
    val memberNickname: String,
    val planId: Long,
    val title: String
)
