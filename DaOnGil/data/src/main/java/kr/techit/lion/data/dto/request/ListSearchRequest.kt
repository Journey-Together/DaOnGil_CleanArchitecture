package kr.techit.lion.data.dto.request

import kr.techit.lion.domain.model.search.ListSearchOption

internal data class ListSearchRequest (
    val category: String?,
    val size: Int,
    val page: Int,
    val query: String?,
    val disabilityType: List<Long>?,
    val detailFilter: List<Long>?,
    val areaCode: String?,
    val sigunguCode: String?,
    val arrange: String
)

internal fun ListSearchOption.toRequestModel() = ListSearchRequest(
    category = category,
    size = size,
    page = page,
    query = query,
    disabilityType = disabilityType,
    detailFilter = detailFilter,
    areaCode = areaCode,
    sigunguCode = sigunguCode,
    arrange = arrange
)
