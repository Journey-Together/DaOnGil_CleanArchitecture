package kr.tekit.lion.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.domain.model.search.RecentlySearchKeyword
import kr.tekit.lion.domain.model.search.RecentlySearchKeywordList

interface RecentlySearchKeywordRepository {
    suspend fun readAllKeyword(): Flow<RecentlySearchKeywordList>
    suspend fun insertKeyword(keyword: RecentlySearchKeyword)
    suspend fun deleteKeyword(id: Long)
    suspend fun deleteAllKeyword()
}