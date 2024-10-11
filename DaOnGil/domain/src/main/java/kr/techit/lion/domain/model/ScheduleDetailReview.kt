package kr.techit.lion.domain.model

data class ScheduleDetailReview(
    val reviewId: Long?,
    val content: String?,
    val grade: Double?,
    val imageList: List<String>?,
    val isWriter: Boolean,
    val hasReview: Boolean,
    val profileUrl: String,
    val isReport: Boolean?
)