package kr.tekit.lion.data.dto.request

import android.graphics.BitmapFactory
import kr.tekit.lion.domain.model.ProfileImg
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

//fun ProfileImg.toMultiPartBody(): MultipartBody.Part {
//    val file = File(this.path)
////    val bitmap = BitmapFactory.decodeFile(file.path).compressBitmap(60)
////    val requestFile = bitmap.toRequestBody("MultiPartFile".toMediaTypeOrNull())
//    return MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
//}