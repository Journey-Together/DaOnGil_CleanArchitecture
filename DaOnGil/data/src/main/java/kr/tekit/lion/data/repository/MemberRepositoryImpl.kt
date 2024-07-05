package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.MemberDataSource
import kr.tekit.lion.data.dto.remote.request.toRequestBody
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.repository.MemberRepository
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberDataSource: MemberDataSource
): MemberRepository {
    override suspend fun updateConcernType(requestBody: ConcernType) {
        return memberDataSource.updateConcernType(requestBody.toRequestBody())
    }
}