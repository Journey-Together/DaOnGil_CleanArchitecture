package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.IceInfo
import kr.tekit.lion.domain.model.MyInfo
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.model.MyDefaultInfo
import kr.tekit.lion.domain.model.PersonalInfo
import kr.tekit.lion.domain.model.ProfileImage
import kr.tekit.lion.domain.exception.Result

interface MemberRepository {

    suspend fun getMyIfo(): Result<MyInfo>

    suspend fun getMyDefaultInfo(): Result<MyDefaultInfo>

    suspend fun modifyMyPersonalInfo(request: PersonalInfo): Result<Unit>

    suspend fun modifyMyProfileImg(request: ProfileImage): Result<Unit>

    suspend fun modifyMyIceInfo(request: IceInfo): Result<Unit>

    suspend fun getConcernType(): Result<ConcernType>

    suspend fun updateConcernType(request: ConcernType): Result<Unit>
}