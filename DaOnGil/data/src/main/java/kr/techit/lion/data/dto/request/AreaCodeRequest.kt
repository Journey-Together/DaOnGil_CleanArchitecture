package kr.techit.lion.data.dto.request

import kr.techit.lion.data.BuildConfig

internal data class AreaCodeRequest(
    val areaCode: String,
){
    fun toRequestModel(): Map<String, String>{
        return mapOf(
            "numOfRows" to "31",
            "MobileOS" to "AND",
            "MobileApp" to "DaOnGil",
            "_type" to "json",
            "serviceKey" to BuildConfig.KOR_API_KEY,
            "areaCode" to areaCode
        )
    }
}