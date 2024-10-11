package kr.techit.lion.data.dto.response.scheduleform

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class PlaceSearchResultsData(
    val placeInfoList: List<PlaceSearchInfoData>,
    val pageNo: Int,
    val pageSize: Int,
    val totalPages: Int,
    val last: Boolean,
    val totalElements: Long
)
