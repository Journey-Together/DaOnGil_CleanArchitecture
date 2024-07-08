package kr.tekit.lion.domain.model

data class ListSearchResult (
    val place: AroundPlace,
)

data class ListSearchResultList(
    val places: List<ListSearchResult>,
    val isLastPage: Boolean = false
)