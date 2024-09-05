package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.PharmacyMapInfo
import kr.tekit.lion.domain.exception.Result

interface PharmacyRepository {
    suspend fun getPharmacy(q0: String?, q1: String?): Result<List<PharmacyMapInfo>>
}