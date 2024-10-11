package kr.techit.lion.data.dto.response.plan.myMainSchedule

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val endDate: String,
    val hasReview: Boolean?,
    val imageUrl: String?,
    val planId: Long,
    val remainDate: String?,
    val startDate: String?,
    val title: String?
)
