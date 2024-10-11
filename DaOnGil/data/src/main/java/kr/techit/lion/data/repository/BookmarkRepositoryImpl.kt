package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.BookmarkDataSource
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.BookmarkedPlace
import kr.techit.lion.domain.model.PlaceBookmark
import kr.techit.lion.domain.model.PlanBookmark
import kr.techit.lion.domain.model.PlanDetailBookmark
import kr.techit.lion.domain.repository.BookmarkRepository
import javax.inject.Inject

internal class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDataSource: BookmarkDataSource
) : BookmarkRepository {

    override suspend fun getPlaceBookmarkList(): Result<List<BookmarkedPlace>> {
        return bookmarkDataSource.getPlaceBookmarkList()
    }

    override suspend fun getPlaceBookmark(): Result<List<PlaceBookmark>> {
        return bookmarkDataSource.getPlaceBookmark()
    }

    override suspend fun getPlanBookmark(): Result<List<PlanBookmark>> {
        return bookmarkDataSource.getPlanBookmark()
    }

    override suspend fun updatePlaceBookmark(placeId: Long): Result<Unit> {
        return bookmarkDataSource.updatePlaceBookmark(placeId)
    }

    override suspend fun updatePlanBookmark(planId: Long): Result<Unit> {
        return bookmarkDataSource.updatePlanBookmark(planId)
    }

    override suspend fun getPlanDetailBookmark(planId: Long): PlanDetailBookmark {
        return bookmarkDataSource.getPlanDetailBookmark(planId)
    }
}