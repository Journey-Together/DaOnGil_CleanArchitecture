package kr.techit.lion.data.service

import kr.techit.lion.data.BuildConfig
import kr.techit.lion.data.dto.response.emergency.basic.EmergencyBasicResponse
import kr.techit.lion.data.dto.response.emergency.message.EmergencyMessageResponse
import kr.techit.lion.data.dto.response.emergency.realtime.EmergencyRealtimeResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface EmergencyService {

    // 응급실 지역 실시간 정보
    @GET("getEmrrmRltmUsefulSckbdInfoInqire")
    suspend fun getEmergencyRealtime(
        @Query("STAGE1") stage1: String?,
        @Query("STAGE2") stage2: String?,
        @Query("numOfRows") numOfRows: Int = 1000,
        @Query("_type") type: String = "json",
        @Query("serviceKey") serviceKey: String = BuildConfig.EMERGENCY_API_KEY
    ) : EmergencyRealtimeResponse

    // 응급실 기본 정보
    @GET("getEgytBassInfoInqire")
    suspend fun getEmergencyBasic(
        @Query("HPID") hpid: String?,
        @Query("_type") type: String = "json",
        @Query("serviceKey") serviceKey: String = BuildConfig.EMERGENCY_API_KEY
    ): EmergencyBasicResponse

    // 응급실 실시간 메세지
    @GET("getEmrrmSrsillDissMsgInqire")
    suspend fun getEmergencyMessage(
        @Query("HPID") hpid: String?,
        @Query("_type") type: String = "json",
        @Query("numOfRows") numOfRows: Int = 10000,
        @Query("serviceKey") serviceKey: String = BuildConfig.EMERGENCY_API_KEY
    ): EmergencyMessageResponse

}