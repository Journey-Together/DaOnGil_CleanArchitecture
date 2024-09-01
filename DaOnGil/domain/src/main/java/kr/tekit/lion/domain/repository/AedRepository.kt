package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.AedMapInfo

interface AedRepository {
    suspend fun getAedInfo(q0: String?, q1: String?) : List<AedMapInfo>
}