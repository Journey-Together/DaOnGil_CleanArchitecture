package kr.tekit.lion.data.service

import kr.tekit.lion.data.dto.response.searchplace.list.SearchPlaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("place/search")
    suspend fun searchPlaceByList(
        @Query("category") category: String,
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Query("query") query: String?,
        @Query("disabilityType") disabilityType: List<Long>?,
        @Query("detailFilter") detailFilter: List<Long>?,
        @Query("areacode") areaCode: String?,
        @Query("sigungucode") sigunguCode: String?,
        @Query("arrange") arrange: String?,
    ): SearchPlaceResponse
}