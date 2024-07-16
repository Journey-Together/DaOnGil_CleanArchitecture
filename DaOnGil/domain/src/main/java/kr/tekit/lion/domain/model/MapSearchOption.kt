package kr.tekit.lion.domain.model

import java.util.TreeSet

data class MapSearchOption (
    val category: String,
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double,
    val disabilityType: TreeSet<Long>? = null,
    val detailFilter: TreeSet<Long>? = null,
    val arrange: String
)