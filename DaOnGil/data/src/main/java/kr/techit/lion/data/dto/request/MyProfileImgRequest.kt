package kr.techit.lion.data.dto.request

import kr.techit.lion.domain.model.ProfileImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun ProfileImage.toMultipartBody(): MultipartBody.Part {
    val requestBody = this.data.toRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("profileImage", "profile.jpg", requestBody)
}