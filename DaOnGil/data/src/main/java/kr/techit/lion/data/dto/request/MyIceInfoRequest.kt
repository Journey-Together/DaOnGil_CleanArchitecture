package kr.techit.lion.data.dto.request

import kr.techit.lion.domain.model.IceInfo
import kr.techit.lion.data.dto.request.util.AdapterProvider.Companion.JsonAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal data class MyIceInfoRequest (
    val bloodType: String,
    val birth: String,
    val disease: String,
    val allergy: String,
    val medication: String,
    val part1_rel: String,
    val part1_phone: String,
    val part2_rel: String,
    val part2_phone: String
)

internal fun IceInfo.toRequestBody(): RequestBody {
    return JsonAdapter(MyIceInfoRequest::class.java).toJson(
        MyIceInfoRequest(
            this.bloodType,
            this.birth,
            this.disease,
            this.allergy,
            this.medication,
            this.part1Rel,
            this.part1Phone,
            this.part2Rel,
            this.part2Phone
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}

