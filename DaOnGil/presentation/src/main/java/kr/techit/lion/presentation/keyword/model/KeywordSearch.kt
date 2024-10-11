package kr.techit.lion.presentation.keyword.model

import kr.techit.lion.domain.model.search.ListSearchOption
import kr.techit.lion.presentation.main.model.SortByLatest

data class KeywordSearch(
    val keyword: String,
    val page: Int,
){
    fun toDomainModel(): ListSearchOption {
        return ListSearchOption(
            category = null,
            page = page,
            size = 0,
            disabilityType = emptyList(),
            detailFilter = emptyList(),
            areaCode = null,
            sigunguCode = null,
            query = keyword,
            arrange = SortByLatest.sortCode
        )
    }
}
