package kr.techit.lion.presentation.home.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.techit.lion.domain.model.MyPlaceReviewInfo
import kr.techit.lion.domain.model.detailplace.Review
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
    val myReview : Boolean,
    val placeName: String
): Parcelable

fun Review.toReviewInfo(placeName: String): ReviewInfo {
    return ReviewInfo(
        reviewId = this.reviewId,
        nickname = this.nickname,
        profileImg = this.profileImg,
        content = this.content,
        reviewImgs = this.reviewImgs,
        grade = this.grade,
        date = this.date,
        myReview = this.myReview,
        placeName = placeName
    )
}

fun ReviewInfo.toMyPlaceReviewInfo(): MyPlaceReviewInfo {
    return MyPlaceReviewInfo(
        reviewId = this.reviewId,
        placeId = 0,
        grade = this.grade,
        name = this.placeName,
        date = this.date,
        images = this.reviewImgs ?: emptyList(),
        isReport = null,
        content = this.content
    )
}