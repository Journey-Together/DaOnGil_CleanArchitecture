package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.ReverseGeocodes
import kr.tekit.lion.domain.exception.Result

interface NaverMapRepository {
    suspend fun getReverseGeoCode(coords: String): Result<ReverseGeocodes>
}