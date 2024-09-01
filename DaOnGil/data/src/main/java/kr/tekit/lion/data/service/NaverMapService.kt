package kr.tekit.lion.data.service

import kr.tekit.lion.data.dto.response.naverMap.ReverseGecodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NaverMapService {
    // Reverse Geocoding (위경도 -> 주소)
    @GET("map-reversegeocode/v2/gc")
    suspend fun getReverseGeoCode(
        @Query("coords") coords: String,
        @Query("orders") orders: String = "admcode",
        @Query("output") output: String = "json"
    ) : ReverseGecodeResponse
}