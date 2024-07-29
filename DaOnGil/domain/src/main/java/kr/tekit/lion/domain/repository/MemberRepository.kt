package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.model.Result

interface MemberRepository {
    suspend fun updateConcernType(requestBody: ConcernType): Result<Unit>
}