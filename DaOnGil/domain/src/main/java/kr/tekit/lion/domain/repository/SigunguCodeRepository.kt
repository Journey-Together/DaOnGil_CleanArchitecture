package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.SigunguCode
import kr.tekit.lion.domain.model.SigunguList

interface SigunguCodeRepository {
    suspend fun getSigunguCodeByVillageName(villageName: String): String?
    suspend fun getAllSigunguCode(code: String): SigunguList
    suspend fun addSigunguCode(sigunguCode: List<SigunguCode>)
}