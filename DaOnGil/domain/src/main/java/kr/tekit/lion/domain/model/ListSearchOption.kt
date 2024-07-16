package kr.tekit.lion.domain.model

import java.util.TreeSet

data class ListSearchOption (
    val category: String,
    val size: Int,
    val page: Int,
    val query: String? = null,
    val disabilityType: TreeSet<Long>? = TreeSet<Long>(setOf(1)),
    val detailFilter: TreeSet<Long>? = TreeSet<Long>(setOf(1, 6, 7, 8, 9)),
    val areaCode: String? = null,
    val sigunguCode: String? = null,
    val arrange: String
)
