package kr.techit.lion.data.mapper

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal fun String.toFullAreaName(): String{
    return when(this){
        "서울" -> "서울특별시"
        "인천" -> "인천광역시"
        "부산" -> "부산광역시"
        "대전" -> "대전광역시"
        "대구" -> "대구광역시"
        "광주" -> "광주광역시"
        "울산" -> "울산광역시"
        "제주도" -> "제주특별자치도"
        else -> this
    }
}

internal fun String.toRequestBody(): RequestBody{
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}