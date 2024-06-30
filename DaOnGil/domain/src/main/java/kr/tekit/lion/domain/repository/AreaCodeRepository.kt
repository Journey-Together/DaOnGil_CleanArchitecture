package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.AreaCode

interface AreaCodeRepository {
    suspend fun getAreaCodeByName(areaName: String): String?
    suspend fun getAllAreaCodes(): List<AreaCode>
    suspend fun addAreaCodeInfo(areaCodeList: List<AreaCode>)
}