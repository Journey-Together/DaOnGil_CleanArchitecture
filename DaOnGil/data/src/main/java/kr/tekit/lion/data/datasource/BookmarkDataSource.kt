package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.common.execute
import kr.tekit.lion.data.service.BookmarkService
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.BookmarkedPlace
import kr.tekit.lion.domain.model.PlaceBookmark
import kr.tekit.lion.domain.model.PlanBookmark
import kr.tekit.lion.domain.model.PlanDetailBookmark
import javax.inject.Inject


internal class BookmarkDataSource @Inject constructor(
    private val bookmarkService: BookmarkService
) {
    suspend fun getPlaceBookmarkList(): Result<List<BookmarkedPlace>> = execute {
        bookmarkService.getPlaceBookmarkList().toDomainModel()
    }

    suspend fun getPlaceBookmark(): Result<List<PlaceBookmark>> = execute {
        bookmarkService.getPlaceBookmark().toDomainModel()
    }

    suspend fun getPlanBookmark(): Result<List<PlanBookmark>> = execute {
        bookmarkService.getPlanBookmark().toDomainModel()
    }

    suspend fun updatePlaceBookmark(placeId: Long) = execute {
        bookmarkService.updatePlaceBookmark(placeId)
    }

    suspend fun updatePlanBookmark(planId: Long) = execute {
        bookmarkService.updatePlanBookmark(planId)
    }

    suspend fun getPlanDetailBookmark(planId: Long): PlanDetailBookmark  {
        return bookmarkService.getPlanDetailBookmark(planId).toDomainModel()
    }
}