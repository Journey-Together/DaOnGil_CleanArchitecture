package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.ReverseGeocodes
import kr.techit.lion.domain.exception.Result

interface NaverMapRepository {
    suspend fun getReverseGeoCode(coords: String): Result<ReverseGeocodes>
}