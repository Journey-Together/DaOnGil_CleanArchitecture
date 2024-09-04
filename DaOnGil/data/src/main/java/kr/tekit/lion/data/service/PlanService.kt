package kr.tekit.lion.data.service


import kr.tekit.lion.data.dto.response.plan.myScheduleElapsed.MyElapsedResponse
import kr.tekit.lion.data.dto.response.plan.myScheduleUpcoming.MyUpcomingsResponse
import kr.tekit.lion.data.dto.response.scheduleform.PlaceSearchResultsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PlanService {
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
}