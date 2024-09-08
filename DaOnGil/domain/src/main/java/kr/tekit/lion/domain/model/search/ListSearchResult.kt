package kr.tekit.lion.domain.model.search

import kr.tekit.lion.domain.model.Place

data class ListSearchResult (
    val place: Place,
)

data class ListSearchResultList(
    val places: List<ListSearchResult>,
    val isLastPage: Boolean,
    val itemSize: Int
)