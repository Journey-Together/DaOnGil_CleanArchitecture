package kr.techit.lion.data.dto.request

import kr.techit.lion.data.dto.request.util.AdapterProvider.Companion.JsonAdapter
import kr.techit.lion.domain.model.schedule.NewScheduleReview
import kr.techit.lion.domain.model.schedule.ReviewImg
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

internal data class ScheduleReviewRequest(
    val grade: Float,
    val content: String,
    val isPublic: Boolean
)

fun NewScheduleReview.toRequestBody() : RequestBody {
    return JsonAdapter(ScheduleReviewRequest::class.java).toJson(
        ScheduleReviewRequest(
            grade = this.grade,
            content = this.content,
            isPublic = this.isPublic
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}

fun List<ReviewImg>.toMultipartBodyList(): List<MultipartBody.Part> {
    return this.map { image ->
        val file = File(image .path)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("images", file.name, requestBody)
    }
}