package kr.techit.lion.data.dto.response.scheduleform

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.scheduleform.PlaceSearchInfoList.PlaceSearchInfo
import kr.techit.lion.domain.model.scheduleform.PlaceSearchInfoList.TotalElementsInfo
import kr.techit.lion.domain.model.scheduleform.PlaceSearchResult

@JsonClass(generateAdapter = true)
internal data class PlaceSearchResultsResponse(
    val code: Int,
    val message: String,
    val data: PlaceSearchResultsData
){
    fun toDomainModel() : PlaceSearchResult {
        return PlaceSearchResult(
            placeInfoList = this.data.placeInfoList.map {
                PlaceSearchInfo(
                    placeId = it.placeId,
                    placeName = it.placeName,
                    category = it.category,
                    imageUrl = it.imageUrl
                )
            },
            pageNo = this.data.pageNo,
            last = this.data.last,
            TotalElementsInfo(
                totalElements = this.data.totalElements
            )
        )
    }
}
