package kr.techit.lion.presentation.schedulereview.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.techit.lion.domain.model.ScheduleDetail

@Parcelize
data class OriginalScheduleReviewInfo(
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
): Parcelable

fun ScheduleDetail.toOriginalScheduleReviewInfoModel(): OriginalScheduleReviewInfo {
    return OriginalScheduleReviewInfo(
        title = this.title,
        startDate = this.startDate,
        endDate = this.endDate,
        imageUrl = this.reviewImages?.firstOrNull() ?: "",
        reviewId = this.reviewId ?: -1,
        content = this.content ?: "",
        grade = this.grade?.toFloat() ?: 0.0F,
        imageList = this.reviewImages ?: emptyList()
    )
}
