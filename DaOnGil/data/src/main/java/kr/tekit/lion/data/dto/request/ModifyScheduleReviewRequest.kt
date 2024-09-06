package kr.tekit.lion.data.dto.request

import kr.tekit.lion.data.dto.request.util.AdapterProvider
import kr.tekit.lion.domain.model.schedule.ModifiedScheduleReview
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal data class ModifyScheduleReviewRequest (
    val grade: Float?,
    val content: String?,
    val deleteImgUrls: List<String>?
)

fun ModifiedScheduleReview.toRequestBody(): RequestBody {
    return AdapterProvider.JsonAdapter(ModifyScheduleReviewRequest::class.java).toJson(
        ModifyScheduleReviewRequest(
            grade = this.grade,
            content = this.content,
            deleteImgUrls = this.deleteImgUrls
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}