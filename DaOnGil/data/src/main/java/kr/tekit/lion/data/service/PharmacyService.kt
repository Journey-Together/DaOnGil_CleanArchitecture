package kr.tekit.lion.data.service

import kr.tekit.lion.data.BuildConfig
import kr.tekit.lion.data.dto.response.pharmacy.PharmacyResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PharmacyService {
    @GET("getParmacyListInfoInqire")
    suspend fun getPharmacy(
        @Query("Q0") q0: String?,
        @Query("Q1") q1: String?,
        @Query("numOfRows") numOfRows: Int = 24534,
        @Query("_type") type: String = "json",
        @Query("serviceKey") serviceKey: String = BuildConfig.EMERGENCY_API_KEY
    ) : PharmacyResponse
}