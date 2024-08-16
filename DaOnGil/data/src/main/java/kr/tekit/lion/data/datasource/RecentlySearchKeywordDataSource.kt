package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.database.dao.RecentlySearchKeywordDAO
import kr.tekit.lion.data.database.entity.RecentlySearchKeywordEntity
import javax.inject.Inject

class RecentlySearchKeywordDataSource @Inject constructor(
    private val recentlySearchKeywordDao: RecentlySearchKeywordDAO
) {
    fun readAllKeyword() = recentlySearchKeywordDao.readAllKeyword()

    suspend fun insertKeyword(keyword: RecentlySearchKeywordEntity) = recentlySearchKeywordDao.insertKeyword(keyword)

    suspend fun deleteKeyword(id: Long) = recentlySearchKeywordDao.deleteKeyword(id)

    suspend fun deleteAllKeyword() = recentlySearchKeywordDao.deleteAllKeyword()
}