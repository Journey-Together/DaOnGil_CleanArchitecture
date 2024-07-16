package kr.tekit.lion.data.dto.request

import kr.tekit.lion.domain.model.MapSearchOption

data class MapSearchRequest (
    val category: String,
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
    val disabilityType: List<Long>?,
    val detailFilter: List<Long>?,
    val arrange: String
)

fun MapSearchOption.toRequestModel() = MapSearchRequest(
    category = category,
    maxX = maxX,
    maxY = maxY,
    minX = minX,
    minY = minY,
    disabilityType = disabilityType?.toList(),
    detailFilter = detailFilter?.toList(),
    arrange = arrange
)

