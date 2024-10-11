package kr.techit.lion.data.datasource

import kr.techit.lion.data.database.dao.SigunguCodeDao
import kr.techit.lion.data.database.entity.SigunguCodeEntity
import javax.inject.Inject

internal class SigunguCodeDatasource @Inject constructor(
    private val sigunguCodeDao: SigunguCodeDao
) {
    suspend fun addSigunguCodeInfoList(villageCodes: List<SigunguCodeEntity>) {
        sigunguCodeDao.setVillageCode(villageCodes)
    }

    fun getSigunguCodeByVillageName(villageName: String, areaCode: String): String?{
        return sigunguCodeDao.getSigunguCodeByVillageName(villageName, areaCode)
    }

    suspend fun getAllSigunguInfoList(code: String): List<SigunguCodeEntity> {
        return sigunguCodeDao.getSigunguCode(code)
    }
}