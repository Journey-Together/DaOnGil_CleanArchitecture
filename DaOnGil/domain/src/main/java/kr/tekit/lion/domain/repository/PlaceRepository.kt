package kr.tekit.lion.domain.repository

import kotlinx.coroutines.flow.Flow
import kr.tekit.lion.domain.model.search.ListSearchOption
import kr.tekit.lion.domain.model.search.ListSearchResultList
import kr.tekit.lion.domain.model.search.MapSearchOption
import kr.tekit.lion.domain.model.search.MapSearchResultList
import kr.tekit.lion.domain.model.Result

interface PlaceRepository {
    suspend fun getSearchPlaceResultByList(request: ListSearchOption): Result<ListSearchResultList>

    fun getSearchPlaceResultByMap(request: MapSearchOption): Flow<MapSearchResultList>
}