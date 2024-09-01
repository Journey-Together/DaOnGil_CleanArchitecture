package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.NaverMapDataSource
import kr.tekit.lion.domain.model.ReverseGecodes
import kr.tekit.lion.domain.repository.NaverMapRepository
import kr.tekit.lion.domain.exception.Result
import javax.inject.Inject

internal class NaverMapRepositoryImpl @Inject constructor(
    private val naverMapDataSource: NaverMapDataSource
): NaverMapRepository  {
    override suspend fun getReverseGeoCode(coords: String): Result<ReverseGecodes> {
        return naverMapDataSource.getReverseGeoCode(coords)
    }

}