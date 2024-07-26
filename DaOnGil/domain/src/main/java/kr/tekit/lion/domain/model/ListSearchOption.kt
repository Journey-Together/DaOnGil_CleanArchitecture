package kr.tekit.lion.domain.model

data class ListSearchOption (
    val category: String,
    val size: Int,
    val page: Int,
    val query: String?,
    val disabilityType: List<Long>?,
    val detailFilter: List<Long>?,
    val areaCode: String?,
    val sigunguCode: String?,
    val arrange: String
)
