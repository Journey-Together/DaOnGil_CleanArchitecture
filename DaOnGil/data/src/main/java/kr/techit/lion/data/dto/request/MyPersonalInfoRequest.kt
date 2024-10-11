package kr.techit.lion.data.dto.request

import kr.techit.lion.data.dto.request.util.AdapterProvider.Companion.JsonAdapter
import kr.techit.lion.domain.model.PersonalInfo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal data class MyPersonalInfoRequest(
    val nickname: String,
    val phone: String
)

internal fun PersonalInfo.toRequestBody(): RequestBody {
    return JsonAdapter(MyPersonalInfoRequest::class.java).toJson(
        MyPersonalInfoRequest(
            this.nickname,
            this.phone
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}