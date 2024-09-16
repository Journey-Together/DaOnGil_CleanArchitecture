package kr.tekit.lion.presentation.main.model

import kr.tekit.lion.domain.model.search.MapSearchOption
import java.util.TreeSet

data class MapOptionState (
    val category: Category,
    val location: Locate,
    val disabilityType: TreeSet<Long>?,
    val detailFilter: TreeSet<Long>?,
    val arrange: String
) {
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

    companion object{
        fun create(): MapOptionState {
            return MapOptionState(
                category = Category.PLACE,
                location = Locate(
                    minLatitude = 0.0,
                    maxLatitude = 0.0,
                    minLongitude = 0.0,
                    maxLongitude = 0.0,
                ),
                disabilityType = DisabilityType.createDisabilityTypeCodes(),
                detailFilter = DisabilityType.createFilterCodes(),
                arrange = SortByLatest.sortCode
            )
        }
    }
}

