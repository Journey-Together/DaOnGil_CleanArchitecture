package kr.techit.lion.data.datasource

import kr.techit.lion.data.service.NaverMapService
import kr.techit.lion.domain.model.ReverseGeocodes
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.data.common.execute
import javax.inject.Inject

internal class NaverMapDataSource @Inject constructor(
    private val naverMapService: NaverMapService
) {
    suspend fun getReverseGeoCode(coords: String): Result<ReverseGeocodes> = execute {
        naverMapService.getReverseGeoCode(coords).toDomainModel()
    }
}