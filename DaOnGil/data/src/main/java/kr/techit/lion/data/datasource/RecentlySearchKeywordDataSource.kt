package kr.techit.lion.data.datasource

import kr.techit.lion.data.database.dao.RecentlySearchKeywordDAO
import kr.techit.lion.data.database.entity.RecentlySearchKeywordEntity
import javax.inject.Inject

internal class RecentlySearchKeywordDataSource @Inject constructor(
    private val recentlySearchKeywordDao: RecentlySearchKeywordDAO
) {
    fun readAllKeyword() = recentlySearchKeywordDao.readAllKeyword()

    suspend fun insertKeyword(keyword: RecentlySearchKeywordEntity) = recentlySearchKeywordDao.insertKeyword(keyword)

    suspend fun deleteKeyword(id: Long) = recentlySearchKeywordDao.deleteKeyword(id)

    suspend fun deleteAllKeyword() = recentlySearchKeywordDao.deleteAllKeyword()
}