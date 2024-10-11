package kr.techit.lion.data.dto.request

import kr.techit.lion.domain.model.search.MapSearchOption

internal data class MapSearchRequest (
    val category: String,
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
    val disabilityType: List<Long>,
    val detailFilter: List<Long>,
    val arrange: String
)

internal fun MapSearchOption.toRequestModel() = MapSearchRequest(
    category = category,
    maxX = maxX,
    maxY = maxY,
    minX = minX,
    minY = minY,
    disabilityType = disabilityType,
    detailFilter = detailFilter,
    arrange = arrange
)

