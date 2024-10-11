package kr.techit.lion.data.service

import kr.techit.lion.data.dto.response.searchplace.auto_complete_keyword.AutoCompleteKeywordResponse
import kr.techit.lion.data.dto.response.myreview.MyPlaceReviewResponse
import kr.techit.lion.data.dto.response.detailplace.DetailPlaceResponse
import kr.techit.lion.data.dto.response.detailplaceguest.DetailPlaceGuestResponse
import kr.techit.lion.data.dto.response.mainplace.MainPlaceResponse
import kr.techit.lion.data.dto.response.placereview.WritePlaceReviewResponse
import kr.techit.lion.data.dto.response.placereviewlist.PlaceReviewResponse
import kr.techit.lion.data.dto.response.placereviewlistguest.PlaceReviewResponseGuest
import kr.techit.lion.data.dto.response.searchplace.list.SearchPlaceResponse
import kr.techit.lion.data.dto.response.searchplace.map.MapSearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Tag

internal interface PlaceService {
    @GET("place/search")
    suspend fun searchByList(
        @Query("category") category: String?,
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Query("query") query: String?,
        @Query("disabilityType") disabilityType: List<Long>?,
        @Query("detailFilter") detailFilter: List<Long>?,
        @Query("areacode") areaCode: String?,
        @Query("sigungucode") sigunguCode: String?,
        @Query("arrange") arrange: String?,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): SearchPlaceResponse

    @GET("place/search/map")
    suspend fun searchByMap(
        @Query("category") category: String,
        @Query("minX") minX: Double,
        @Query("maxX") maxX: Double,
        @Query("minY") minY: Double,
        @Query("maxY") maxY: Double,
        @Query("disabilityType") disabilityType: List<Long>?,
        @Query("detailFilter") detailFilter: List<Long>?,
        @Query("arrange") arrange: String?,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): MapSearchResponse

    @GET("place/search/autocomplete")
    suspend fun getAutoCompleteKeyword(
        @Query("query") keyword: String,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): AutoCompleteKeywordResponse

    @GET("place/review/my")
    suspend fun getMyPlaceReview(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): MyPlaceReviewResponse

    @DELETE("place/review/my/{reviewId}")
    suspend fun deleteMyPlaceReview(
        @Path("reviewId") reviewId: Long
    )

    @Multipart
    @PATCH("place/review/my/{reviewId}")
    suspend fun updateMyPlaceReviewData(
        @Path("reviewId") reviewId: Long,
        @Part("updateReviewDto") reviewUpdateReq: RequestBody,
        @Part addImages: List<MultipartBody.Part>
    )

    @GET("place/main")
    suspend fun getPlaceMainInfo(
        @Query("areacode") areacode : String,
        @Query("sigungucode") sigungucode : String,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): MainPlaceResponse

    @GET("place/{placeId}")
    suspend fun getPlaceDetailInfo(
        @Path("placeId") placeId: Long,
        @Tag authType: AuthType = AuthType.ACCESS_TOKEN
    ): DetailPlaceResponse

    @GET("place/guest/{placeId}")
    suspend fun getPlaceDetailInfoGuest(
        @Path("placeId") placeId: Long,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): DetailPlaceGuestResponse

    @Multipart
    @POST("place/review/{placeId}")
    suspend fun writePlaceReviewData(
        @Path("placeId") placeId: Long,
        @Part("placeReviewReq") placeReviewReq: RequestBody,
        @Part images : List<MultipartBody.Part>?
    ): WritePlaceReviewResponse

    @GET("place/review/{placeId}")
    suspend fun getPlaceReviewList(
        @Path("placeId") placeId: Long,
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Tag authType: AuthType = AuthType.ACCESS_TOKEN
    ): PlaceReviewResponse

    @GET("place/review/guest/{placeId}")
    suspend fun getPlaceReviewListGuest(
        @Path("placeId") placeId: Long,
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): PlaceReviewResponseGuest
}