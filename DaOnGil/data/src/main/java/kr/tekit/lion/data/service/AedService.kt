package kr.tekit.lion.data.service

import kr.tekit.lion.data.BuildConfig
import kr.tekit.lion.data.dto.response.aed.AedResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface AedService {
    @GET("getEgytAedManageInfoInqire")
    suspend fun getAedInfo(
        @Query("Q0") q0: String?,
        @Query("Q1") q1: String?,
        @Query("numOfRows") numOfRows: Int = 48568,
        @Query("_type") type: String = "json",
        @Query("serviceKey") serviceKey: String = BuildConfig.EMERGENCY_API_KEY
    ) : AedResponse
}