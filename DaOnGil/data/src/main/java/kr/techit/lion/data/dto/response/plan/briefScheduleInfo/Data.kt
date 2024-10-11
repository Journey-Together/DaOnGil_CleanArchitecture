package kr.techit.lion.data.dto.response.plan.briefScheduleInfo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val planId: Long,
    val title: String?,
    val startDate: String?,
    val endDate: String?,
    val imageUrl: String?,
)
