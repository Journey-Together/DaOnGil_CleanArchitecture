package kr.techit.lion.domain.model.schedule

data class MyElapsedScheduleInfo(
    val planId: Long,
    val title: String,
    val startDate: String,
    val endDate: String,
    val imageUrl: String,
    val hasReview: Boolean,
)
