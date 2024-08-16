package kr.tekit.lion.domain.model.search

data class RecentlySearchKeyword (
    val id: Long? = null,
    val keyword: String
)

fun String.toRecentlySearchKeyword() = RecentlySearchKeyword(keyword = this)