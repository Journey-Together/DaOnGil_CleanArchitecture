package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.service.MemberService
import kr.tekit.lion.data.common.execute
import okhttp3.RequestBody
import javax.inject.Inject

internal class MemberDataSource @Inject constructor(
    private val memberService: MemberService
) {
    suspend fun updateConcernType(requestBody: RequestBody) = execute{
        memberService.updateConcernType(requestBody)
    }
}