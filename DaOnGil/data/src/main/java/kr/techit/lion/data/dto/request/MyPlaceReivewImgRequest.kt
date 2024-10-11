package kr.techit.lion.data.dto.request

import kr.techit.lion.domain.model.MyPlaceReviewImages
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun MyPlaceReviewImages.toMultipartBody(): List<MultipartBody.Part> {
    return this.addImages?.map { imagePath ->
        val file = File(imagePath)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("addImages", file.name, requestBody)
    } ?: emptyList()
}