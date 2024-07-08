package kr.tekit.lion.data.dto.remote.request

import kr.tekit.lion.domain.model.ListSearchOption

data class SearchByListRequest (
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

fun ListSearchOption.toRequestModel() = SearchByListRequest(
    category = category,
    size = size,
    page = page,
    query = query,
    disabilityType = disabilityType?.toList(),
    detailFilter = detailFilter?.toList(),
    areaCode = areaCode,
    sigunguCode = sigunguCode,
    arrange = arrange
)
