package kr.techit.lion.data.dto.response.plan.scheduleDetailReview

internal data class Data(
    val reviewId: Long?,
    val content: String?,
    val grade: Double?,
    val imageList: List<String>?,
    val isWriter: Boolean,
    val hasReview: Boolean,
    val profileUrl: String,
    val isReport: Boolean?
)