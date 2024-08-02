package kr.tekit.lion.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.tekit.lion.data.datasource.PlaceDataSource
import kr.tekit.lion.data.dto.request.toRequestModel
import kr.tekit.lion.domain.model.search.ListSearchOption
import kr.tekit.lion.domain.model.search.ListSearchResultList
import kr.tekit.lion.domain.model.search.MapSearchOption
import kr.tekit.lion.domain.model.search.MapSearchResultList
import kr.tekit.lion.domain.repository.PlaceRepository
import javax.inject.Inject
import kr.tekit.lion.domain.model.Result

internal class PlaceRepositoryImpl @Inject constructor(
    private val placeDataSource: PlaceDataSource
) : PlaceRepository {

    override suspend fun getSearchPlaceResultByList(request: ListSearchOption)
    : Result<ListSearchResultList> {
        return placeDataSource.searchByList(request.toRequestModel())
    }

    override fun getSearchPlaceResultByMap(request: MapSearchOption): Flow<MapSearchResultList> = flow{
        val response = placeDataSource.searchByMap(request.toRequestModel())
        emit(response.toDomainModel())
    }
}