package kr.techit.lion.domain.model.search

import kr.techit.lion.domain.model.Place

data class ListSearchResult (
    val place: Place,
)

data class ListSearchResultList(
    val places: List<ListSearchResult>,
    val isLastPage: Boolean,
    val itemSize: Int
)