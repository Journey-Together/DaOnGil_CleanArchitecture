package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.PlaceDataSource
import kr.tekit.lion.data.dto.request.toRequestModel
import kr.tekit.lion.domain.model.ListSearchOption
import kr.tekit.lion.domain.model.ListSearchResultList
import kr.tekit.lion.domain.repository.PlaceRepository
import javax.inject.Inject
import kr.tekit.lion.domain.model.Result

class PlaceRepositoryImpl @Inject constructor(
    private val placeDataSource: PlaceDataSource
) : PlaceRepository {

    override suspend fun getSearchPlaceResultByList(request: ListSearchOption)
    : Result<ListSearchResultList> {
        return placeDataSource.searchPlaceByList(request.toRequestModel())
    }
}