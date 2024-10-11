package kr.techit.lion.data.service

import kr.techit.lion.daongil.data.dto.remote.response.bookmark.PlaceBookmarkListResponse
import kr.techit.lion.daongil.data.dto.remote.response.bookmark.PlaceBookmarkResponse
import kr.techit.lion.daongil.data.dto.remote.response.bookmark.PlanBookmarkResponse
import kr.techit.lion.data.dto.response.bookmark.PlanDetailBookmarkResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

internal interface BookmarkService {
    @GET("bookmark/names")
    suspend fun getPlaceBookmarkList() : PlaceBookmarkListResponse

    @GET("bookmark/place")
    suspend fun getPlaceBookmark() : PlaceBookmarkResponse

    @GET("bookmark/plan")
    suspend fun getPlanBookmark() : PlanBookmarkResponse

    @PATCH("bookmark/place/{placeId}")
    suspend fun updatePlaceBookmark(
        @Path("placeId") placeId: Long
    )

    @PATCH("bookmark/plan/{planId}")
    suspend fun updatePlanBookmark(
        @Path("planId") planId: Long
    )

    @GET("bookmark/plan/{planId}")
    suspend fun getPlanDetailBookmark(
        @Path("planId") planId: Long
    ): PlanDetailBookmarkResponse
}