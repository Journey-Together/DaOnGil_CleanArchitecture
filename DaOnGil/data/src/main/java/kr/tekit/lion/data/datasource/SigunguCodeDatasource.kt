package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.database.dao.SigunguCodeDao
import kr.tekit.lion.data.database.entity.SigunguCodeEntity
import javax.inject.Inject

class SigunguCodeDatasource @Inject constructor(
    private val sigunguCodeDao: SigunguCodeDao
) {
    suspend fun addSigunguCodeInfoList(villageCodes: List<SigunguCodeEntity>) {
        sigunguCodeDao.setVillageCode(villageCodes)
    }

    suspend fun getSigunguCodeByVillageName(villageName: String): String?{
        return sigunguCodeDao.getSigunguCodeByVillageName(villageName)
    }

    suspend fun getAllSigunguInfoList(code: String): List<SigunguCodeEntity> {
        return sigunguCodeDao.getSigunguCode(code)
    }
}