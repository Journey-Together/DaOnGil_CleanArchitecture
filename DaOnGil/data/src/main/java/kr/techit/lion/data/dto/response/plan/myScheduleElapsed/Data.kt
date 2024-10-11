package kr.techit.lion.data.dto.response.plan.myScheduleElapsed

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val planResList: List<PlanRes>,
    val pageNo: Int,
    val pageSize: Int,
    val totalPages: Int,
    val last: Boolean,
)