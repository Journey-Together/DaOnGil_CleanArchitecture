package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.AreaCode

interface KorWithRepository {
    suspend fun getAreaCodeInfo(): List<AreaCode>
    suspend fun getSigunguCode(areaCode: String): List<AreaCode>
}