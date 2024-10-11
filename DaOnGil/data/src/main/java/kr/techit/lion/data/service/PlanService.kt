package kr.techit.lion.data.service


import kr.techit.lion.data.dto.response.plan.briefScheduleInfo.BriefScheduleInfoResponse
import kr.techit.lion.data.dto.response.plan.scheduleDetailInfo.ScheduleDetailResponse
import kr.techit.lion.data.dto.response.plan.myMainSchedule.MyMainScheduleResponse
import kr.techit.lion.data.dto.response.plan.myScheduleElapsed.MyElapsedResponse
import kr.techit.lion.data.dto.response.plan.myScheduleUpcoming.MyUpcomingsResponse
import kr.techit.lion.data.dto.response.scheduleform.PlaceSearchResultsResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import kr.techit.lion.data.dto.response.plan.openSchedule.OpenPlanListResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import kr.techit.lion.data.dto.response.plan.scheduleDetailReview.ScheduleDetailReviewResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Tag

internal interface PlanService {
    // 다가오는 일정 목록
    @GET("plan/my/not-complete")
    suspend fun getMyUpcomingScheduleList(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): MyUpcomingsResponse

    // 다녀온 일정 목록
    @GET("plan/my/complete")
    suspend fun getMyElapsedScheduleList(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): MyElapsedResponse


    // 장소명 기반 여행지 검색 : size - 한 페이지 데이터 수, page - 페이지 넘버
    @GET("plan/search")
    suspend fun getPlaceSearchResults(
        @Query("word") word: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ) : PlaceSearchResultsResponse

    // 새로운 여행 일정 등록
    @POST("plan")
    suspend fun addNewPlan(
        @Body newPlan: RequestBody,
        @Tag authType: AuthType = AuthType.ACCESS_TOKEN
    )

    // 일정 수정
    @PATCH("plan/{planId}")
    suspend fun modifySchedule(
        @Path("planId") planId: Long,
        @Body newPlan: RequestBody
    )

    // 내 일정 정보
    @GET("plan/my")
    suspend fun getMyMainSchedule(): MyMainScheduleResponse

    // 공개 일정 정보
    @GET("plan/open")
    suspend fun getOpenPlanList(
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Tag authType: AuthType = AuthType.NO_AUTH,
    ): OpenPlanListResponse

    // 일정 간단한 정보 (한 개)
    @GET("plan/{planId}")
    suspend fun getBriefScheduleInfo(
        @Path("planId") planId: Long
    ) : BriefScheduleInfoResponse

    // 여행 일정 후기 작성 (사진을 첨부한 경우)
    @Multipart
    @POST("plan/review/{planId}")
    suspend fun addNewScheduleReview(
        @Path("planId") planId: Long,
        @Part("planReviewReq") scheduleReview: RequestBody,
        @Part images: List<MultipartBody.Part>
    )

    // 여행 일정 후기 작성
    @Multipart
    @POST("plan/review/{planId}")
    suspend fun addNewScheduleReviewTextOnly(
        @Path("planId") planId: Long,
        @Part("planReviewReq") scheduleReview: RequestBody,
    )

    // 여행 일정 후기 수정
    @Multipart
    @PATCH("plan/review/{reviewId}")
    suspend fun modifyScheduleReview(
        @Path("reviewId") reviewId: Long,
        @Part("planReviewReq") scheduleReview: RequestBody,
        @Part images: List<MultipartBody.Part>
    )

    // 여행 일정 후기 수정 (이미지 제외)
    @Multipart
    @PATCH("plan/review/{reviewId}")
    suspend fun modifyScheduleReviewTextOnly(
        @Path("reviewId") reviewId: Long,
        @Part("planReviewReq") scheduleReview: RequestBody
    )
      
    // 여행 일정 상세보기 (로그인버전)
    @GET("plan/detail/{planId}")
    suspend fun getDetailScheduleInfo(
        @Path("planId") planId: Long
    ): ScheduleDetailResponse

    // 여행 일정 상세보기 (게스트버전)
    @GET("plan/guest/detail/{planId}")
    suspend fun getDetailScheduleInfoGuest(
        @Path("planId") planId: Long,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): ScheduleDetailResponse

    // 여행 일정 상세보기 페이지에서의 여행 일정 후기 정보 (로그인 버전)
    @GET("plan/review/{planId}")
    suspend fun getDetailScheduleReview(
        @Path("planId") planId: Long
    ): ScheduleDetailReviewResponse

    // 여행 일정 상세보기 페이지에서의 여행 일정 후기 정보 (게스트 버전)
    @GET("plan/guest/review/{planId}")
    suspend fun getDetailScheduleReviewGuest(
        @Path("planId") planId: Long,
        @Tag authType: AuthType = AuthType.NO_AUTH
    ): ScheduleDetailReviewResponse

    // 여행 일정 후기 삭제
    @DELETE("plan/review/{reviewId}")
    suspend fun deleteMyPlanReview(
        @Path("reviewId") reviewId: Long
    )

    // 일정 공개 비공개 수정
    @PATCH("plan/{planId}/ispublic")
    suspend fun updateMyPlanPublic(
        @Path("planId") planId: Long
    )

    // 여행 일정 삭제
    @DELETE("plan/{planId}")
    suspend fun deleteMyPlanSchedule(
        @Path("planId") planId: Long
    )
}