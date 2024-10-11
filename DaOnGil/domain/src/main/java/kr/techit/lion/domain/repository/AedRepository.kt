package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.AedMapInfo

interface AedRepository {
    suspend fun getAedInfo(q0: String?, q1: String?) : List<AedMapInfo>
}