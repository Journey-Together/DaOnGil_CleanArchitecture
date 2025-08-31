package kr.techit.lion.data.repository

import kr.techit.lion.domain.model.IceInfo
import kr.techit.lion.domain.model.MyInfo
import kr.techit.lion.data.datasource.MemberDataSource
import kr.techit.lion.data.dto.request.toRequestBody
import kr.techit.lion.data.dto.request.toMultipartBody
import kr.techit.lion.domain.model.concern.Concerns
import kr.techit.lion.domain.model.MyDefaultInfo
import kr.techit.lion.domain.model.PersonalInfo
import kr.techit.lion.domain.model.ProfileImage
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.repository.MemberRepository
import javax.inject.Inject

internal class MemberRepositoryImpl @Inject constructor(
    private val memberDataSource: MemberDataSource
): MemberRepository {

    override suspend fun getMyIfo(): Result<MyInfo> {
        return memberDataSource.getMyInfo()
    }

    override suspend fun getMyDefaultInfo(): Result<MyDefaultInfo> {
        return memberDataSource.getMyDefaultInfo()
    }

    override suspend fun modifyMyPersonalInfo(request: PersonalInfo): Result<Unit> {
        return memberDataSource.modifyMyPersonalInfo(request.toRequestBody())
    }

    override suspend fun modifyMyProfileImg(request: ProfileImage): Result<Unit> {
        return memberDataSource.modifyMyProfileImage(request.toMultipartBody())
    }

    override suspend fun modifyMyIceInfo(request: IceInfo): Result<Unit> {
        return memberDataSource.modifyMyIceInfo(request.toRequestBody())
    }

    override suspend fun getConcernType(): Result<Concerns> {
        return memberDataSource.getConcernType()
    }

    override suspend fun updateConcernType(request: Concerns): Result<Unit> {
        return memberDataSource.updateConcernType(request.toRequestBody())
    }
}
