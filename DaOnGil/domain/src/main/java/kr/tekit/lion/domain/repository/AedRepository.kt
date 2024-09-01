package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.AedMapInfo

interface AedRepository {
    suspend fun getAedInfo(q0: String?, q1: String?) : Result<List<AedMapInfo>>
}