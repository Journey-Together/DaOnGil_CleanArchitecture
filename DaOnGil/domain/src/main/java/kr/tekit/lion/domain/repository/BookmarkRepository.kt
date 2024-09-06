package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.BookmarkedPlace
import kr.tekit.lion.domain.model.PlaceBookmark
import kr.tekit.lion.domain.model.PlanBookmark
import kr.tekit.lion.domain.model.PlanDetailBookmark

interface BookmarkRepository {
    suspend fun getPlaceBookmarkList(): Result<List<BookmarkedPlace>>

    suspend fun getPlaceBookmark(): Result<List<PlaceBookmark>>

    suspend fun getPlanBookmark(): Result<List<PlanBookmark>>

    suspend fun updatePlaceBookmark(placeId: Long): Result<Unit>

    suspend fun updatePlanBookmark(planId: Long): Result<Unit>

    suspend fun getPlanDetailBookmark(planId: Long): PlanDetailBookmark
}