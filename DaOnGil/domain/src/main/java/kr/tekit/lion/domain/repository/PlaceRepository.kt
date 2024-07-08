package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.ListSearchOption
import kr.tekit.lion.domain.model.ListSearchResultList
import kr.tekit.lion.domain.model.Result

interface PlaceRepository {
    suspend fun getSearchPlaceResultByList(request: ListSearchOption): Result<ListSearchResultList>
}