package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.database.entity.AreaCodeEntity
import kr.tekit.lion.data.database.dao.AreaCodeDao
import javax.inject.Inject

internal class AreaCodeDataSource @Inject constructor(
    private val areaCodeDao: AreaCodeDao,
) {
    suspend fun getAllAreaCodes(): List<AreaCodeEntity> {
        return areaCodeDao.getAreaCodes()
    }

    fun getAreaCodeInfo(code: String): String? {
        return areaCodeDao.getAreaCode(code)
    }

    suspend fun addAreaCodeInfoList(areaCodeEntity: List<AreaCodeEntity>) {
      areaCodeDao.insertAreaCode(areaCodeEntity)
    }
}