package kr.tekit.lion.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.domain.model.search.ListSearchOption
import kr.tekit.lion.domain.model.search.ListSearchResultList
import kr.tekit.lion.domain.model.search.MapSearchOption
import kr.tekit.lion.domain.model.search.MapSearchResultList
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.MyPlaceReview
import kr.tekit.lion.domain.model.MyPlaceReviewImages
import kr.tekit.lion.domain.model.UpdateMyPlaceReview
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfo
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfoGuest
import kr.tekit.lion.domain.model.mainplace.PlaceMainInfo
import kr.tekit.lion.domain.model.placereview.NewReviewData
import kr.tekit.lion.domain.model.placereview.NewReviewImages
import kr.tekit.lion.domain.model.placereview.WritePlaceReview
import kr.tekit.lion.domain.model.search.AutoCompleteKeyword

interface PlaceRepository {
    suspend fun getSearchPlaceResultByList(request: ListSearchOption): Result<ListSearchResultList>

    fun getSearchPlaceResultByMap(request: MapSearchOption): Flow<MapSearchResultList>

    suspend fun getAutoCompleteKeyword(keyword: String): Flow<AutoCompleteKeyword>

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
}