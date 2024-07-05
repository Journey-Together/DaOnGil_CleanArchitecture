package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.ConcernType

interface MemberRepository {
    suspend fun updateConcernType(requestBody: ConcernType)
}