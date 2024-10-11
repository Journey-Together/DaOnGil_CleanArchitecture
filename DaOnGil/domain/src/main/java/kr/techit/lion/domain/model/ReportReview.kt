package kr.techit.lion.domain.model

data class ReportReview(
    val reviewId: Long,
    val reason: String,
    val description: String?
)