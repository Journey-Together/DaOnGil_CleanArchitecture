package kr.techit.lion.domain.repository

import kr.techit.lion.domain.model.IceInfo
import kr.techit.lion.domain.model.MyInfo
import kr.techit.lion.domain.model.ConcernType
import kr.techit.lion.domain.model.MyDefaultInfo
import kr.techit.lion.domain.model.PersonalInfo
import kr.techit.lion.domain.model.ProfileImage
import kr.techit.lion.domain.exception.Result

interface MemberRepository {

    suspend fun getMyIfo(): Result<MyInfo>

    suspend fun getMyDefaultInfo(): Result<MyDefaultInfo>

    suspend fun modifyMyPersonalInfo(request: PersonalInfo): Result<Unit>

    suspend fun modifyMyProfileImg(request: ProfileImage): Result<Unit>

    suspend fun modifyMyIceInfo(request: IceInfo): Result<Unit>

    suspend fun getConcernType(): Result<ConcernType>

    suspend fun updateConcernType(request: ConcernType): Result<Unit>
}