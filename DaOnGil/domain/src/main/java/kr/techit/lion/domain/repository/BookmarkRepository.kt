package kr.techit.lion.domain.repository

import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.BookmarkedPlace
import kr.techit.lion.domain.model.PlaceBookmark
import kr.techit.lion.domain.model.PlanBookmark
import kr.techit.lion.domain.model.PlanDetailBookmark

interface BookmarkRepository {
    suspend fun getPlaceBookmarkList(): Result<List<BookmarkedPlace>>

    suspend fun getPlaceBookmark(): Result<List<PlaceBookmark>>

    suspend fun getPlanBookmark(): Result<List<PlanBookmark>>

    suspend fun updatePlaceBookmark(placeId: Long): Result<Unit>

    suspend fun updatePlanBookmark(planId: Long): Result<Unit>

    suspend fun getPlanDetailBookmark(planId: Long): PlanDetailBookmark
}