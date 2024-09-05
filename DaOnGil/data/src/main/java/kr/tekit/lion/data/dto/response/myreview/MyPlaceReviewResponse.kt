package kr.tekit.lion.data.dto.response.myreview

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.MyPlaceReviewInfo
import kr.tekit.lion.domain.model.MyPlaceReview

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