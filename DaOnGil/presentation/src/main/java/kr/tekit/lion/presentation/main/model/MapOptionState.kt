package kr.tekit.lion.presentation.main.model

import kr.tekit.lion.domain.model.MapSearchOption
import java.util.TreeSet

data class MapOptionState (
    val category: Category,
    val location: Locate,
    val disabilityType: TreeSet<Long>?,
    val detailFilter: TreeSet<Long>?,
    val arrange: String
){
    fun toDomainModel(): MapSearchOption {
        return MapSearchOption(
            category = category.name,
            minX = location.minLongitude,
            maxX = location.maxLongitude,
            minY = location.minLatitude,
            maxY = location.maxLatitude,
            disabilityType = disabilityType?.toList() ?: emptyList(),
            detailFilter = detailFilter?.toList() ?: emptyList(),
            arrange = arrange
        )
    }
}

