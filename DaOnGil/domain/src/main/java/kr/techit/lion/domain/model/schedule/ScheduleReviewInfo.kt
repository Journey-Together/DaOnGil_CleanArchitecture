package kr.techit.lion.domain.model.schedule

data class ScheduleReviewInfo(
    // 여행 일정 정보
    val title: String,
    val startDate: String,
    val endDate: String,
    val imageUrl: String,
    // 리뷰 정보
    val reviewId: Long,
    val content: String,
    val grade: Float,
    val imageList: List<String>?,
)
