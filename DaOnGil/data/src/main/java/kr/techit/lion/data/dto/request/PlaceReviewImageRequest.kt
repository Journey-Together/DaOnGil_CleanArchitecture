package kr.techit.lion.data.dto.request

import kr.techit.lion.domain.model.placereview.NewReviewImages
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun NewReviewImages.toMultiPartBody(): List<MultipartBody.Part> {
    val parts = mutableListOf<MultipartBody.Part>()

    this.images?.let { images ->
        for (imageUrl in images) {
            val requestBody = imageUrl.toRequestBody("MultiPartFile".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("images", "reviewImg.jpg", requestBody)
            parts.add(part)
        }
    }

    return parts
}
