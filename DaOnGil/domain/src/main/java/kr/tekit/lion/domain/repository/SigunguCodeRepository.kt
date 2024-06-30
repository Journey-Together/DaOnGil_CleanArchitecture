package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.SigunguCode

interface SigunguCodeRepository {
    suspend fun getSigunguCodeByVillageName(villageName: String): String?
    suspend fun getAllSigunguCode(code: String): List<SigunguCode>
    suspend fun addSigunguCode(sigunguCode: List<SigunguCode>)
}