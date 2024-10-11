package kr.techit.lion.domain.model.scheduleform

import kr.techit.lion.domain.model.scheduleform.PlaceSearchInfoList as Result
data class PlaceSearchResult(
    val placeInfoList: List<Result.PlaceSearchInfo>,
    val pageNo: Int,
    val last: Boolean,
    val totalElements: Result.TotalElementsInfo
)
