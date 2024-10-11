package kr.techit.lion.data.repository

import kr.techit.lion.data.database.entity.toDomainModel
import kr.techit.lion.data.database.entity.toEntity
import kr.techit.lion.data.datasource.SigunguCodeDatasource
import kr.techit.lion.domain.model.area.SigunguCode
import kr.techit.lion.domain.model.area.SigunguCodeList
import kr.techit.lion.domain.repository.SigunguCodeRepository
import javax.inject.Inject

internal class SigunguCodeRepositoryImpl @Inject constructor(
    private val sigunguCodeDatasource: SigunguCodeDatasource
) : SigunguCodeRepository {

    override fun getSigunguCodeByVillageName(villageName: String, areaCode: String): String? {
        return sigunguCodeDatasource.getSigunguCodeByVillageName(villageName, areaCode)
    }

    override suspend fun getAllSigunguCode(code: String): SigunguCodeList {
        return SigunguCodeList(
            sigunguCodeDatasource.getAllSigunguInfoList(code).map {
                it.toDomainModel()
            }
        )
    }

    override suspend fun addSigunguCode(sigunguCode: List<SigunguCode>) {
        sigunguCodeDatasource.addSigunguCodeInfoList(sigunguCode.map { it.toEntity() })
    }
}