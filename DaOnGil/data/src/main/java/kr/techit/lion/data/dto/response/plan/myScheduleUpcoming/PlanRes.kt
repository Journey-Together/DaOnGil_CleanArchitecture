package kr.techit.lion.data.dto.response.plan.myScheduleUpcoming

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PlanRes(
    val planId: Long,
    val title: String,
    val startDate: String,
    val endDate: String,
    val imageUrl: String?,
     val remainDate: String,
)