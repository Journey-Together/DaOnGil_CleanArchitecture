package kr.tekit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.tekit.lion.data.dto.request.toRequestBody
import kr.tekit.lion.data.datasource.PlaceDataSource
import kr.tekit.lion.data.dto.request.toMultipartBody
import kr.tekit.lion.data.dto.request.toRequestModel
import kr.tekit.lion.domain.model.search.ListSearchOption
import kr.tekit.lion.domain.model.search.ListSearchResultList
import kr.tekit.lion.domain.model.search.MapSearchOption
import kr.tekit.lion.domain.model.search.MapSearchResultList
import kr.tekit.lion.domain.repository.PlaceRepository
import javax.inject.Inject
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.MyPlaceReview
import kr.tekit.lion.domain.model.MyPlaceReviewImages
import kr.tekit.lion.domain.model.UpdateMyPlaceReview
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfo
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfoGuest
import kr.tekit.lion.domain.model.mainplace.PlaceMainInfo
import kr.tekit.lion.domain.model.search.AutoCompleteKeyword

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

    override suspend fun getAutoCompleteKeyword(keyword: String): Flow<AutoCompleteKeyword> = flow {
        val response = placeDataSource.getAutoCompleteKeyword(keyword)
        emit(response.toDomainModel())
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

    override suspend fun getPlaceMainInfo(areaCode: String, sigunguCode: String): Result<PlaceMainInfo> {
        return placeDataSource.getPlaceMainInfo(areaCode, sigunguCode)
    }

    override suspend fun getPlaceDetailInfo(placeId: Long): Result<PlaceDetailInfo> {
        return placeDataSource.getPlaceDetailInfo(placeId)
    }

    override suspend fun getPlaceDetailInfoGuest(placeId: Long): Result<PlaceDetailInfoGuest> {
        return placeDataSource.getPlaceDetailInfoGuest(placeId)
    }
}