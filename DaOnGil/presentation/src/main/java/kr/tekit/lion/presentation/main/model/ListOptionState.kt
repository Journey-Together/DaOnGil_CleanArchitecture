package kr.tekit.lion.presentation.main.model

import kr.tekit.lion.domain.model.ListSearchOption
import java.util.TreeSet

data class ListOptionState(
    val category: Category,
    val size: Int,
    val page: Int,
    val query: String? = null,
    val disabilityType: TreeSet<Long>? = TreeSet<Long>(setOf(1)),
    val detailFilter: TreeSet<Long>? = TreeSet<Long>(setOf(1, 6, 7, 8, 9)),
    val areaCode: String? = null,
    val sigunguCode: String? = null,
    val arrange: String
){
    fun toDomainModel(): ListSearchOption{
        return ListSearchOption(
            category = category.name,
            page = page,
            size = size,
            disabilityType = disabilityType?.toList() ?: emptyList(),
            detailFilter = detailFilter?.toList() ?: emptyList(),
            areaCode = areaCode,
            sigunguCode = sigunguCode,
            query = query,
            arrange = arrange
        )
    }
}