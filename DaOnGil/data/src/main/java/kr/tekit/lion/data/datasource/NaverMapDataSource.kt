package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.service.NaverMapService
import kr.tekit.lion.domain.model.ReverseGeocodes
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.data.common.execute
import javax.inject.Inject

internal class NaverMapDataSource @Inject constructor(
    private val naverMapService: NaverMapService
) {
    suspend fun getReverseGeoCode(coords: String): Result<ReverseGeocodes> = execute {
        naverMapService.getReverseGeoCode(coords).toDomainModel()
    }
}