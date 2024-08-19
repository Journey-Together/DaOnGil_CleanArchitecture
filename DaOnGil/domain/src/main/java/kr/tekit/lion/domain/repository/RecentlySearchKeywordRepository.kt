package kr.tekit.lion.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.domain.model.search.RecentlySearchKeyword

interface RecentlySearchKeywordRepository {
    suspend fun readAllKeyword(): Flow<List<RecentlySearchKeyword>>
    suspend fun insertKeyword(keyword: RecentlySearchKeyword)
    suspend fun deleteKeyword(id: Long)
    suspend fun deleteAllKeyword()
}