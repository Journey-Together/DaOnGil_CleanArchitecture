package kr.techit.lion.data.dto.request

import kr.techit.lion.data.dto.request.util.AdapterProvider
import kr.techit.lion.domain.model.schedule.ModifiedScheduleReview
import kr.techit.lion.domain.model.schedule.ReviewImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

fun List<ReviewImage>.toMultipartBodyList(): List<MultipartBody.Part> {
    return this.map { image ->
        val file = image.imagePath?.let { File(it) }
        val requestBody = file?.asRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("images", file?.name, requestBody!!)
    }
    }