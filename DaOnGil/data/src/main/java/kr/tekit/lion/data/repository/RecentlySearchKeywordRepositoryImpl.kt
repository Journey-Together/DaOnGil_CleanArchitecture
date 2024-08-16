package kr.tekit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kr.tekit.lion.data.database.entity.toDomainModel
import kr.tekit.lion.data.database.entity.toEntity
import kr.tekit.lion.data.datasource.RecentlySearchKeywordDataSource
import kr.tekit.lion.domain.model.search.RecentlySearchKeyword
import kr.tekit.lion.domain.repository.RecentlySearchKeywordRepository
import javax.inject.Inject

class RecentlySearchKeywordRepositoryImpl @Inject constructor(
    private val recentlySearchKeywordDataSource: RecentlySearchKeywordDataSource
): RecentlySearchKeywordRepository {

    override suspend fun readAllKeyword(): Flow<List<RecentlySearchKeyword>> =
        recentlySearchKeywordDataSource.readAllKeyword().map { list ->
            list.map { it.toDomainModel() }
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