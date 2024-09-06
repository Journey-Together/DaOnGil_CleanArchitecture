package kr.tekit.lion.data.service


import kr.tekit.lion.data.dto.response.plan.briefScheduleInfo.BriefScheduleInfoResponse
import kr.tekit.lion.data.dto.response.plan.myMainSchedule.MyMainScheduleResponse
import kr.tekit.lion.data.dto.response.plan.myScheduleElapsed.MyElapsedResponse
import kr.tekit.lion.data.dto.response.plan.myScheduleUpcoming.MyUpcomingsResponse
import kr.tekit.lion.data.dto.response.scheduleform.PlaceSearchResultsResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import kr.tekit.lion.data.dto.response.plan.openSchedule.OpenPlanListResponse
import retrofit2.http.GET
import retrofit2.http.POST
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
}