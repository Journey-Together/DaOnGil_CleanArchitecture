package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.PharmacyDataSource
import kr.techit.lion.domain.model.PharmacyMapInfo
import kr.techit.lion.domain.repository.PharmacyRepository
import kr.techit.lion.domain.exception.Result
import javax.inject.Inject

internal class PharmacyRepositoryImpl @Inject constructor(
    private val pharmacyDataSource: PharmacyDataSource
): PharmacyRepository {
    override suspend fun getPharmacy(q0: String?, q1: String?): Result<List<PharmacyMapInfo>> {
       return pharmacyDataSource.getPharmacy(q0, q1)
    }

}