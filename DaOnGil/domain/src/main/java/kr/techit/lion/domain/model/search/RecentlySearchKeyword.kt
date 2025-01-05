package kr.techit.lion.domain.model.search

data class RecentlySearchKeyword (
    val id: Long? = null,
    val keyword: String
)

data class RecentlySearchKeywordList(
    val list: List<RecentlySearchKeyword>
)

fun RecentlySearchKeywordList.findKeyword(keyword: String): RecentlySearchKeyword? {
    return this.list.firstOrNull { it.keyword == keyword }
}

fun String.toRecentlySearchKeyword() = RecentlySearchKeyword(keyword = this)