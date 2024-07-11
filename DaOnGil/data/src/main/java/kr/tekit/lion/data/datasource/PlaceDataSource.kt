package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.dto.remote.request.SearchByListRequest
import kr.tekit.lion.data.dto.remote.response.searchplace.list.toDomainModel
import kr.tekit.lion.data.service.PlaceService
import kr.tekit.lion.data.util.NetworkHandler
import kr.tekit.lion.domain.model.ListSearchResultList
import javax.inject.Inject

class PlaceDataSource @Inject constructor(
    private val placeService: PlaceService
): NetworkHandler() {
    suspend fun searchPlaceByList(request: SearchByListRequest) = execute {
        val response = placeService.searchPlaceByList(
            category = request.category,
            size = request.size,
            page = request.page,
            query = request.query,
            disabilityType = request.disabilityType,
            detailFilter = request.detailFilter,
            areaCode = request.areaCode,
            sigunguCode = request.sigunguCode,
            arrange = request.arrange
        )

        val result = if ((request.page * 10) == response.data.totalPages) {
            ListSearchResultList(response.toDomainModel(), true)
        } else {
            ListSearchResultList(response.toDomainModel(), false)
        }

        result
    }
}
