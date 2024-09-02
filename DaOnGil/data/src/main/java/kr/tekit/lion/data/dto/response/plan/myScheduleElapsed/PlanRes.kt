package kr.tekit.lion.data.dto.response.plan.myScheduleElapsed

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlanRes(
    val planId: Long,
    val title: String,
    val startDate: String,
    val endDate: String,
    val imageUrl: String?,
    val hasReview: Boolean,
)
