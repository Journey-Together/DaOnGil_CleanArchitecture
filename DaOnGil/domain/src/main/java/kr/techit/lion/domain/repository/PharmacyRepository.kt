package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.PharmacyMapInfo
import kr.techit.lion.domain.exception.Result

interface PharmacyRepository {
    suspend fun getPharmacy(q0: String?, q1: String?): Result<List<PharmacyMapInfo>>
}