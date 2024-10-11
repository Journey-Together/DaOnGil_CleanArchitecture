package kr.techit.lion.data.dto.response.placereviewlist

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.placereviewlist.PlaceReview
import kr.techit.lion.domain.model.placereviewlist.PlaceReviewInfo

@JsonClass(generateAdapter = true)
internal data class PlaceReviewResponse(
    val code: Int,
    val data: Data,
    val message: String
) {
    fun toDomainModel(): PlaceReviewInfo {
        return PlaceReviewInfo(
            placeReviewList = data.myPlaceReviewList.map {
                PlaceReview(
                    content = it.content,
                    date = it.date,
                    grade = it.grade,
                    imageList = it.imageList,
                    nickname = it.nickname,
                    profileImg = it.profileImg,
                    reviewId = it.reviewId,
                    myReview = it.myReview
                )
            },
            placeImg = data.placeImg,
            pageNo = data.pageNo,
            pageSize = data.pageSize,
            placeAddress = data.placeAddress,
            placeName = data.placeName,
            totalPages = data.totalPages,
            reviewNum = data.reviewNum
        )
    }
}