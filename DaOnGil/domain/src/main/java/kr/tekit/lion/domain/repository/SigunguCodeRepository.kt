package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.area.SigunguCode
import kr.tekit.lion.domain.model.area.SigunguCodeList

interface SigunguCodeRepository {
    fun getSigunguCodeByVillageName(villageName: String, areaCode: String): String?
    suspend fun getAllSigunguCode(code: String): SigunguCodeList
    suspend fun addSigunguCode(sigunguCode: List<SigunguCode>)
}