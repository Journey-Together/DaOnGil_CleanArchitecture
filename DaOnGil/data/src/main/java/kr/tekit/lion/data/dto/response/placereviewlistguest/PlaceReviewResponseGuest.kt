package kr.tekit.lion.data.dto.response.placereviewlistguest

import kr.tekit.lion.data.dto.response.placereviewlist.Data
import kr.tekit.lion.domain.model.placereviewlist.PlaceReview
import kr.tekit.lion.domain.model.placereviewlist.PlaceReviewInfo

internal data class PlaceReviewResponseGuest(
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
            totalPages = data.totalPages
        )
    }
}