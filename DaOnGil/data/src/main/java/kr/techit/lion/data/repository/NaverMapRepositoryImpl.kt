package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.NaverMapDataSource
import kr.techit.lion.domain.model.ReverseGeocodes
import kr.techit.lion.domain.repository.NaverMapRepository
import kr.techit.lion.domain.exception.Result
import javax.inject.Inject

internal class NaverMapRepositoryImpl @Inject constructor(
    private val naverMapDataSource: NaverMapDataSource
): NaverMapRepository  {
    override suspend fun getReverseGeoCode(coords: String): Result<ReverseGeocodes> {
        return naverMapDataSource.getReverseGeoCode(coords)
    }

}