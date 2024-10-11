package kr.techit.lion.data.dto.response.myreview

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.MyPlaceReviewInfo
import kr.techit.lion.domain.model.MyPlaceReview

@JsonClass(generateAdapter = true)
internal data class MyPlaceReviewResponse(
    val code: Int,
    val data: MyPlaceReviewData,
    val message: String
) {
    fun toDomainModel(): MyPlaceReview {
        return MyPlaceReview(
            myPlaceReviewInfoList = data.myPlaceReviewDtoList.map {
                MyPlaceReviewInfo(
                    content = it.content,
                    date = it.date,
                    grade = it.grade,
                    images = it.images,
                    isReport = it.isReport,
                    name = it.name,
                    placeId = it.placeId,
                    reviewId = it.reviewId
                )
            },
            pageNo = data.pageNo,
            pageSize = data.pageSize,
            reviewNum = data.reviewNum,
            totalPages = data.totalPages
        )
    }
}