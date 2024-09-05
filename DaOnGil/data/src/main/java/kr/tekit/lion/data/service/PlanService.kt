package kr.tekit.lion.data.service


import kr.tekit.lion.data.dto.response.plan.myMainSchedule.MyMainScheduleResponse
import kr.tekit.lion.data.dto.response.plan.myScheduleElapsed.MyElapsedResponse
import kr.tekit.lion.data.dto.response.plan.myScheduleUpcoming.MyUpcomingsResponse
import kr.tekit.lion.data.dto.response.plan.openSchedule.OpenPlanListResponse
import retrofit2.http.GET
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
}