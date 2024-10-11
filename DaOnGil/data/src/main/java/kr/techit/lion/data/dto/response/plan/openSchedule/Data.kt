package kr.techit.lion.data.dto.response.plan.openSchedule

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val last: Boolean,
    val openPlanResList: List<OpenPlanRes>,
    val pageNo: Int,
    val pageSize: Int,
    val totalPages: Int
)
