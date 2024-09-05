package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.service.PharmacyService
import kr.tekit.lion.domain.model.PharmacyMapInfo
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.data.common.execute
import javax.inject.Inject

internal class PharmacyDataSource @Inject constructor(
    private val pharmacyService: PharmacyService
) {
    suspend fun getPharmacy(q0: String?, q1: String?): Result<List<PharmacyMapInfo>> = execute {
        pharmacyService.getPharmacy(q0, q1).toDomainModel()
    }
}