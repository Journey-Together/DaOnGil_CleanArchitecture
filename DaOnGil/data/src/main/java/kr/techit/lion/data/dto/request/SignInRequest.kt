package kr.techit.lion.data.dto.request

import kr.techit.lion.data.dto.request.util.AdapterProvider.Companion.JsonAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class SignInRequest(
    val refreshToken: String
)

internal fun SignInRequest.toRequestBody(): RequestBody {
    return JsonAdapter(SignInRequest::class.java).toJson(
        SignInRequest(
            this.refreshToken
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}