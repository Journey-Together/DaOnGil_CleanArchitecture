package kr.tekit.lion.data.datasource

import kr.tekit.lion.domain.model.MyInfo
import kr.tekit.lion.data.service.MemberService
import kr.tekit.lion.data.common.execute
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.domain.model.MyDefaultInfo
import kr.tekit.lion.domain.model.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

internal class MemberDataSource @Inject constructor(
    private val memberService: MemberService
) {
    suspend fun getMyInfo(): Result<MyInfo> = execute{
        memberService.getMyInfo().toDomainModel()
    }

    suspend fun getMyDefaultInfo(): Result<MyDefaultInfo> = execute{
        memberService.getMyDefaultInfo().toDomainModel()
    }

    suspend fun modifyMyPersonalInfo(request: RequestBody) = execute{
        memberService.modifyMyPersonalInfo(request)
    }

    suspend fun modifyMyProfileImage(request: MultipartBody.Part) = execute{
        memberService.modifyMyProfileImage(request)
    }

    suspend fun modifyMyIceInfo(request: RequestBody) = execute{
        memberService.modifyMyIceInfo(request)
    }

    suspend fun getConcernType(): Result<ConcernType> = execute{
        memberService.getConcernType().toDomainModel()
    }

    suspend fun updateConcernType(requestBody: RequestBody) = execute{
         memberService.updateConcernType(requestBody)
    }
}