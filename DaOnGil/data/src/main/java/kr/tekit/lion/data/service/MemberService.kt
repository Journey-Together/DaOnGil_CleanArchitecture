package kr.tekit.lion.data.service

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.PATCH

internal interface MemberService {
    @PATCH("member/interest-type")
    suspend fun updateConcernType(
        @Body requestBody: RequestBody
    )
}