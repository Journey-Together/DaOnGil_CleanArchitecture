package kr.tekit.lion.data.service

import kr.tekit.lion.data.dto.response.searchplace.list.SearchPlaceResponse
import kr.tekit.lion.data.dto.response.searchplace.map.MapSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PlaceService {
    @GET("place/search")
    suspend fun searchByList(
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
    ): MapSearchResponse
}