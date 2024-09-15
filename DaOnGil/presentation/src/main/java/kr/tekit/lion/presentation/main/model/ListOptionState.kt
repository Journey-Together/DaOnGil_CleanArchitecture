package kr.tekit.lion.presentation.main.model

import kr.tekit.lion.domain.model.search.ListSearchOption
import java.util.TreeSet

data class ListOptionState(
    val category: Category?,
    val page: Int,
    val disabilityType: TreeSet<Long>?,
    val detailFilter: TreeSet<Long>?,
    val areaCode: String?,
    val sigunguCode: String?,
    val arrange: String
){
    fun toDomainModel(): ListSearchOption {
        return ListSearchOption(
            category = category?.name,
            page = page,
            size = 0,
            disabilityType = disabilityType?.toList() ?: emptyList(),
            detailFilter = detailFilter?.toList() ?: emptyList(),
            areaCode = areaCode,
            sigunguCode = sigunguCode,
            query = null,
            arrange = arrange
        )
    }

    companion object {
        fun create(): ListOptionState {
            return ListOptionState(
                category = Category.PLACE,
                page = 0,
                disabilityType = TreeSet(),
                detailFilter = TreeSet(),
                areaCode = null,
                sigunguCode = null,
                arrange = SortByLatest.sortCode
            )
        }
    }
}