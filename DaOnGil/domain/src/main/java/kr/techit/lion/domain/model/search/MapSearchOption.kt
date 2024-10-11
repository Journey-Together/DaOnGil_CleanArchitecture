package kr.techit.lion.domain.model.search

data class MapSearchOption (
    val category: String,
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
    val disabilityType: List<Long>,
    val detailFilter: List<Long>,
    val arrange: String
)