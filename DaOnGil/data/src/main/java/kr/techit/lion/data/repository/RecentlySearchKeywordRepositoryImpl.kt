package kr.techit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.techit.lion.data.database.entity.toDomainModel
import kr.techit.lion.data.database.entity.toEntity
import kr.techit.lion.data.datasource.RecentlySearchKeywordDataSource
import kr.techit.lion.domain.model.search.RecentlySearchKeyword
import kr.techit.lion.domain.model.search.RecentlySearchKeywordList
import kr.techit.lion.domain.repository.RecentlySearchKeywordRepository
import javax.inject.Inject

internal class RecentlySearchKeywordRepositoryImpl @Inject constructor(
    private val recentlySearchKeywordDataSource: RecentlySearchKeywordDataSource
) : RecentlySearchKeywordRepository {

    override suspend fun readAllKeyword(): Flow<RecentlySearchKeywordList> =
        recentlySearchKeywordDataSource.readAllKeyword().map { list ->
            RecentlySearchKeywordList(
                list.map { it.toDomainModel() }
            )
        }

    override suspend fun insertKeyword(keyword: RecentlySearchKeyword) {
        recentlySearchKeywordDataSource.insertKeyword(keyword.toEntity())
    }

    override suspend fun deleteKeyword(id: Long) {
        recentlySearchKeywordDataSource.deleteKeyword(id)
    }

    override suspend fun deleteAllKeyword() {
        recentlySearchKeywordDataSource.deleteAllKeyword()
    }
}