package kr.techit.lion.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.techit.lion.domain.model.search.RecentlySearchKeyword

@Entity(tableName = "recent_search_keyword_table")
internal data class RecentlySearchKeywordEntity (
    val keyword: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

internal fun RecentlySearchKeywordEntity.toDomainModel() =
    RecentlySearchKeyword(
        id = id,
        keyword = keyword
    )

internal fun RecentlySearchKeyword.toEntity(): RecentlySearchKeywordEntity =
    RecentlySearchKeywordEntity(
        keyword = keyword
    )

