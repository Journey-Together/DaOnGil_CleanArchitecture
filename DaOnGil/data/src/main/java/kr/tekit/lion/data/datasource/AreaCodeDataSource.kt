package kr.tekit.lion.data.datasource

import kr.tekit.lion.daongil.data.dto.local.AreaCodeEntity
import kr.tekit.lion.data.database.dao.AreaCodeDao
import javax.inject.Inject

class AreaCodeDataSource @Inject constructor(
    private val areaCodeDao: AreaCodeDao,
) {
    suspend fun getAllAreaCodes(): List<AreaCodeEntity> {
        return areaCodeDao.getAreaCodes()
    }

    suspend fun getAreaCodeInfo(code: String): String? {
        return areaCodeDao.getAreaCode(code)
    }

    suspend fun addAreaCodeInfoList(areaCodeEntity: List<AreaCodeEntity>) {
      areaCodeDao.insertAreaCode(areaCodeEntity)
    }
}