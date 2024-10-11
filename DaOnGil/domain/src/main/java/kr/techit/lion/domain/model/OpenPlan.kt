package kr.techit.lion.domain.model

data class OpenPlan(
    val last: Boolean,
    val openPlanList: List<OpenPlanInfo>,
    val pageNo: Int,
    val totalPages: Int
)
