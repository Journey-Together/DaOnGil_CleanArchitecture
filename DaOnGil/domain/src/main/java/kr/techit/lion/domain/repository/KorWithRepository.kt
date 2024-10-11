package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.area.AreaCode

interface KorWithRepository {
    suspend fun getAreaCodeInfo(): List<AreaCode>
    suspend fun getSigunguCode(areaCode: String): List<AreaCode>
}