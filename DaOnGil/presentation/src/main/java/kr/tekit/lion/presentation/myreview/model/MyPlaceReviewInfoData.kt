package kr.tekit.lion.presentation.myreview.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.tekit.lion.domain.model.MyPlaceReviewInfo
import java.time.LocalDate

@Parcelize
data class MyPlaceReviewInfoData (
    val content: String,
    val date: LocalDate,
    val grade: Float,
    val images: List<String>,
    val name: String,
    val placeId: Long,
    val reviewId: Long
) : Parcelable

fun MyPlaceReviewInfo.toMyPlaceReviewInfoData(): MyPlaceReviewInfoData {
    return MyPlaceReviewInfoData(
        content = this.content,
        date = this.date,
        grade = this.grade,
        images = this.images,
        name = this.name,
        placeId = this.placeId,
        reviewId = this.reviewId
    )
}

fun MyPlaceReviewInfoData.toMyPlaceReviewInfo(): MyPlaceReviewInfo {
    return MyPlaceReviewInfo(
        content = this.content,
        date = this.date,
        grade = this.grade,
        images = this.images,
        name = this.name,
        placeId = this.placeId,
        reviewId = this.reviewId
    )
}