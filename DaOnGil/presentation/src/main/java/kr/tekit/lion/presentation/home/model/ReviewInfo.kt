package kr.tekit.lion.presentation.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.tekit.lion.domain.model.detailplace.Review
import java.time.LocalDate

@Parcelize
data class ReviewInfo (
    val reviewId: Long,
    val nickname : String,
    val profileImg : String,
    val content : String,
    val reviewImgs : List<String>?,
    val grade : Float,
    val date : LocalDate,
    val myReview : Boolean
): Parcelable

fun Review.toReviewInfo(): ReviewInfo {
    return ReviewInfo(
        reviewId = this.reviewId,
        nickname = this.nickname,
        profileImg = this.profileImg,
        content = this.content,
        reviewImgs = this.reviewImgs,
        grade = this.grade,
        date = this.date,
        myReview = this.myReview
    )
}