package kr.techit.lion.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.techit.lion.domain.model.search.ListSearchOption
import kr.techit.lion.domain.model.search.ListSearchResultList
import kr.techit.lion.domain.model.search.MapSearchOption
import kr.techit.lion.domain.model.search.MapSearchResultList
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.MyPlaceReview
import kr.techit.lion.domain.model.MyPlaceReviewImages
import kr.techit.lion.domain.model.UpdateMyPlaceReview
import kr.techit.lion.domain.model.detailplace.PlaceDetailInfo
import kr.techit.lion.domain.model.detailplace.PlaceDetailInfoGuest
import kr.techit.lion.domain.model.mainplace.PlaceMainInfo
import kr.techit.lion.domain.model.placereview.NewReviewData
import kr.techit.lion.domain.model.placereview.NewReviewImages
import kr.techit.lion.domain.model.placereview.WritePlaceReview
import kr.techit.lion.domain.model.placereviewlist.PlaceReviewInfo
import kr.techit.lion.domain.model.search.AutoCompleteKeyword

interface PlaceRepository {
    suspend fun getSearchPlaceResultByList(request: ListSearchOption): Result<ListSearchResultList>

    fun getSearchPlaceResultByMap(request: MapSearchOption): Flow<MapSearchResultList>

    suspend fun getAutoCompleteKeyword(keyword: String): Flow<List<AutoCompleteKeyword>>

    suspend fun getMyPlaceReview(size: Int, page: Int): Result<MyPlaceReview>

    suspend fun deleteMyPlaceReview(reviewId: Long): Result<Unit>

    suspend fun updateMyPlaceReviewData(
        reviewId: Long,
        updateMyPlaceReview: UpdateMyPlaceReview,
        myPlaceReviewImages: MyPlaceReviewImages
    ): Result<Unit>

    suspend fun getPlaceMainInfo(areaCode: String, sigunguCode: String): Result<PlaceMainInfo>

    suspend fun getPlaceDetailInfo(placeId: Long): Result<PlaceDetailInfo>

    suspend fun getPlaceDetailInfoGuest(placeId: Long): Result<PlaceDetailInfoGuest>

    suspend fun writePlaceReviewData(
        placeId: Long,
        newReviewData: NewReviewData,
        reviewImages: NewReviewImages
    ): Result<WritePlaceReview>

    suspend fun getPlaceReviewList(placeId: Long, size: Int, page: Int): Result<PlaceReviewInfo>

    suspend fun getPlaceReviewListGuest(placeId: Long, size: Int, page: Int): Result<PlaceReviewInfo>

}