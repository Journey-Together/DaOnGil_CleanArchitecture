package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.dto.request.ListSearchRequest
import kr.tekit.lion.data.dto.request.MapSearchRequest
import kr.tekit.lion.data.dto.response.searchplace.list.toDomainModel
import kr.tekit.lion.data.service.PlaceService
import kr.tekit.lion.data.common.execute
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.MyPlaceReview
import kr.tekit.lion.domain.model.search.ListSearchResultList
import kr.tekit.lion.domain.model.mainplace.PlaceMainInfo
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfo
import kr.tekit.lion.domain.model.detailplace.PlaceDetailInfoGuest
import kr.tekit.lion.domain.model.placereview.WritePlaceReview
import kr.tekit.lion.domain.model.placereviewlist.PlaceReviewInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

internal class PlaceDataSource @Inject constructor(
    private val placeService: PlaceService
){
    suspend fun searchByList(request: ListSearchRequest) = execute {

        val response = placeService.searchByList(
            category = request.category,
            size = request.size,
            page = request.page,
            query = request.query,
            disabilityType = request.disabilityType,
            detailFilter = request.detailFilter,
            areaCode = request.areaCode,
            sigunguCode = request.sigunguCode,
            arrange = request.arrange
        )

        if ((request.page * 10) < response.data.totalSize) {
            ListSearchResultList(response.toDomainModel(), false, response.data.totalSize)
        } else {
            ListSearchResultList(response.toDomainModel(), true, response.data.totalSize)
        }
    }

    suspend fun searchByMap(request: MapSearchRequest) = placeService.searchByMap(
        category = request.category,
        minX = request.minX,
        minY = request.minY,
        maxX = request.maxX,
        maxY = request.maxY,
        disabilityType = request.disabilityType,
        detailFilter = request.detailFilter,
        arrange = request.arrange
    )

    suspend fun getAutoCompleteKeyword(keyword: String) = placeService.getAutoCompleteKeyword(keyword)
    
    suspend fun getMyPlaceReview(size: Int, page: Int): Result<MyPlaceReview> = execute {
        placeService.getMyPlaceReview(size, page).toDomainModel()
    }

    suspend fun deleteMyPlaceReview(reviewId: Long) = execute {
        placeService.deleteMyPlaceReview(reviewId)
    }

    suspend fun updateMyPlaceReviewData(
        reviewId: Long,
        reviewUpdateReq: RequestBody,
        addImages: List<MultipartBody.Part>
    ) = execute {
        placeService.updateMyPlaceReviewData(reviewId, reviewUpdateReq, addImages)
    }

    suspend fun getPlaceMainInfo(areaCode: String, sigunguCode: String): Result<PlaceMainInfo> = execute {
        placeService.getPlaceMainInfo(areaCode, sigunguCode).toDomainModel()
    }

    suspend fun getPlaceDetailInfo(placeId: Long): Result<PlaceDetailInfo> = execute {
        placeService.getPlaceDetailInfo(placeId).toDomainModel()
    }

    suspend fun getPlaceDetailInfoGuest(placeId: Long): Result<PlaceDetailInfoGuest> = execute {
        placeService.getPlaceDetailInfoGuest(placeId).toDomainModel()
    }

    suspend fun writePlaceReviewData(
        placeId: Long,
        placeReviewReq: RequestBody,
        images: List<MultipartBody.Part>
    ): Result<WritePlaceReview> = execute {
        placeService.writePlaceReviewData(placeId, placeReviewReq, images).toDomainModel()
    }

    suspend fun getPlaceReviewList(placeId: Long, size: Int, page: Int) : Result<PlaceReviewInfo> = execute {
        placeService.getPlaceReviewList(placeId, size, page).toDomainModel()
    }

    suspend fun getPlaceReviewListGuest(placeId: Long, size: Int, page: Int) : Result<PlaceReviewInfo> = execute {
        placeService.getPlaceReviewListGuest(placeId, size, page).toDomainModel()
    }
}
