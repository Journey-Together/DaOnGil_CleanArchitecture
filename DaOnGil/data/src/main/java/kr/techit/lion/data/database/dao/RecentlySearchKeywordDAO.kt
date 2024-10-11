package kr.techit.lion.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kr.techit.lion.data.database.entity.RecentlySearchKeywordEntity

@Dao
internal interface RecentlySearchKeywordDAO {
    @Query("SELECT * FROM recent_search_keyword_table")
    fun readAllKeyword(): Flow<List<RecentlySearchKeywordEntity>>

    @Insert
    suspend fun insertKeyword(keyword: RecentlySearchKeywordEntity)

    @Query("DELETE FROM recent_search_keyword_table where id = :id ")
    suspend fun deleteKeyword(id: Long)

    @Query("DELETE FROM recent_search_keyword_table")
    suspend fun deleteAllKeyword()
}