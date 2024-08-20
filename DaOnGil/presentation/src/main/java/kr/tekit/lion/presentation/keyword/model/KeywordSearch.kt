package kr.tekit.lion.presentation.keyword.model

import kr.tekit.lion.domain.model.search.ListSearchOption
import kr.tekit.lion.presentation.main.model.SortByLatest

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
