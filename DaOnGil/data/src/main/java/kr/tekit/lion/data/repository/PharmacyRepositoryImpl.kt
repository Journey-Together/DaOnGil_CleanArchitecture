package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.PharmacyDataSource
import kr.tekit.lion.domain.model.PharmacyMapInfo
import kr.tekit.lion.domain.repository.PharmacyRepository
import kr.tekit.lion.domain.exception.Result
import javax.inject.Inject

internal class PharmacyRepositoryImpl @Inject constructor(
    private val pharmacyDataSource: PharmacyDataSource
): PharmacyRepository {
    override suspend fun getPharmacy(q0: String?, q1: String?): Result<List<PharmacyMapInfo>> {
       return pharmacyDataSource.getPharmacy(q0, q1)
    }

}