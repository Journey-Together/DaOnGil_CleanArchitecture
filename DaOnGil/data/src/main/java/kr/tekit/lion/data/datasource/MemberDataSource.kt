package kr.tekit.lion.data.datasource

import kr.tekit.lion.data.service.MemberService
import okhttp3.RequestBody
import javax.inject.Inject

class MemberDataSource @Inject constructor(
    private val memberService: MemberService
) {
    suspend fun updateConcernType(requestBody: RequestBody) {
        return memberService.updateConcernType(requestBody)
    }
}