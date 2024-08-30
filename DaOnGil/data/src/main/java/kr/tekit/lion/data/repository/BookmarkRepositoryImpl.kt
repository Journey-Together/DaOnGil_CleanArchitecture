package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.BookmarkDataSource
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.BookmarkedPlace
import kr.tekit.lion.domain.model.PlaceBookmark
import kr.tekit.lion.domain.model.PlanBookmark
import kr.tekit.lion.domain.repository.BookmarkRepository
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
}