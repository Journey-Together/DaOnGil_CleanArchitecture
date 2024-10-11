package kr.techit.lion.data.dto.request

import kr.techit.lion.data.dto.request.util.AdapterProvider.Companion.JsonAdapter
import kr.techit.lion.domain.model.ReportReview
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal data class ReportReviewRequest(
    val review_id: Long,
    val reason: String,
    val description: String?
)

fun ReportReview.toRequestBody(): RequestBody {
    return JsonAdapter(ReportReviewRequest::class.java).toJson(
        ReportReviewRequest(
            this.reviewId,
            this.reason,
            this.description
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}