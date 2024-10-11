package kr.techit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.techit.lion.data.datasource.PlaceDataSource
import kr.techit.lion.data.dto.request.toMultiPartBody
import kr.techit.lion.data.dto.request.toMultipartBody
import kr.techit.lion.data.dto.request.toRequestBody
import kr.techit.lion.data.dto.request.toRequestModel
import kr.techit.lion.domain.model.search.ListSearchOption
import kr.techit.lion.domain.model.search.ListSearchResultList
import kr.techit.lion.domain.model.search.MapSearchOption
import kr.techit.lion.domain.model.search.MapSearchResultList
import kr.techit.lion.domain.repository.PlaceRepository
import javax.inject.Inject
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

internal class PlaceRepositoryImpl @Inject constructor(
    private val placeDataSource: PlaceDataSource
) : PlaceRepository {

    override suspend fun getSearchPlaceResultByList(request: ListSearchOption)
    : Result<ListSearchResultList> {
        return placeDataSource.searchByList(request.toRequestModel())
    }

    override fun getSearchPlaceResultByMap(request: MapSearchOption): Flow<MapSearchResultList> = flow{
        val response = placeDataSource.searchByMap(request.toRequestModel())
        emit(response.toDomainModel())
    }

    override suspend fun getAutoCompleteKeyword(keyword: String): Flow<List<AutoCompleteKeyword>> = flow {
        val response = placeDataSource.getAutoCompleteKeyword(keyword).toDomainModel()
        emit(response)
    }

    override suspend fun getMyPlaceReview(size: Int, page: Int): Result<MyPlaceReview> {
        return placeDataSource.getMyPlaceReview(size, page)
    }

    override suspend fun deleteMyPlaceReview(reviewId: Long): Result<Unit> {
        return placeDataSource.deleteMyPlaceReview(reviewId)
    }

    override suspend fun updateMyPlaceReviewData(
        reviewId: Long,
        updateMyPlaceReview: UpdateMyPlaceReview,
        myPlaceReviewImages: MyPlaceReviewImages
    ): Result<Unit> {
        return placeDataSource.updateMyPlaceReviewData(
            reviewId,
            updateMyPlaceReview.toRequestBody(),
            myPlaceReviewImages.toMultipartBody()
        )
    }

    override suspend fun getPlaceMainInfo(
        areaCode: String,
        sigunguCode: String
    ): Result<PlaceMainInfo> {
        return placeDataSource.getPlaceMainInfo(areaCode, sigunguCode)
    }

    override suspend fun getPlaceDetailInfo(placeId: Long): Result<PlaceDetailInfo> {
        return placeDataSource.getPlaceDetailInfo(placeId)
    }

    override suspend fun getPlaceDetailInfoGuest(placeId: Long): Result<PlaceDetailInfoGuest> {
        return placeDataSource.getPlaceDetailInfoGuest(placeId)
    }

    override suspend fun writePlaceReviewData(
        placeId: Long,
        newReviewData: NewReviewData,
        reviewImages: NewReviewImages
    ): Result<WritePlaceReview> {
        return placeDataSource.writePlaceReviewData(
            placeId,
            newReviewData.toRequestBody(),
            reviewImages.toMultiPartBody()
        )
    }

    override suspend fun getPlaceReviewList(placeId: Long, page: Int, size: Int): Result<PlaceReviewInfo> {
        return placeDataSource.getPlaceReviewList(placeId, page, size)
    }

    override suspend fun getPlaceReviewListGuest(placeId: Long, page: Int, size: Int): Result<PlaceReviewInfo> {
        return placeDataSource.getPlaceReviewListGuest(placeId, page, size)
    }
}