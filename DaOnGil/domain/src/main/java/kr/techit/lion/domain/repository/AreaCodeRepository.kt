package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.area.AreaCode
import kr.techit.lion.domain.model.area.AreaCodeList

interface AreaCodeRepository {
    fun getAreaCodeByName(areaName: String): String?
    suspend fun getAllAreaCodes(): AreaCodeList
    suspend fun addAreaCodeInfo(areaCodeList: List<AreaCode>)
}