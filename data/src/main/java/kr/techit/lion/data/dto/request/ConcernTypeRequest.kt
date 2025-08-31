package kr.techit.lion.data.dto.request

import kr.techit.lion.data.dto.request.util.AdapterProvider.Companion.JsonAdapter
import kr.techit.lion.domain.model.concern.Concerns
import kr.techit.lion.domain.model.concern.ConcernType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal data class ConcernTypeRequest(
    val isPhysical: Boolean,
    val isHear: Boolean,
    val isVisual: Boolean,
    val isElderly: Boolean,
    val isChild: Boolean,
)

fun Concerns.toRequestBody(): RequestBody {
    return JsonAdapter(ConcernTypeRequest::class.java).toJson(
        ConcernTypeRequest(
            this[ConcernType.Physical],
            this[ConcernType.Hear],
            this[ConcernType.Visual],
            this[ConcernType.Elderly],
            this[ConcernType.Child],
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}
