package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.area.AreaCode
import kr.tekit.lion.domain.model.area.AreaCodeList

interface AreaCodeRepository {
    fun getAreaCodeByName(areaName: String): String?
    suspend fun getAllAreaCodes(): AreaCodeList
    suspend fun addAreaCodeInfo(areaCodeList: List<AreaCode>)
}