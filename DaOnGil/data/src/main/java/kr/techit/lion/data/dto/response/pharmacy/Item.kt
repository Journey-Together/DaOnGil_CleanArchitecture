package kr.techit.lion.data.dto.response.pharmacy


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Item(
    val dutyAddr: String?,
    val dutyEtc: String?,
    val dutyMapimg: String?,
    val dutyName: String?,
    val dutyTel1: String?,
    val dutyTime1c: Int?,
    val dutyTime1s: String?,
    val dutyTime2c: Int?,
    val dutyTime2s: String?,
    val dutyTime3c: Int?,
    val dutyTime3s: String?,
    val dutyTime4c: Int?,
    val dutyTime4s: String?,
    val dutyTime5c: Int?,
    val dutyTime5s: String?,
    val dutyTime6c: Int?,
    val dutyTime6s: String?,
    val dutyTime7c: Int?,
    val dutyTime7s: Int?,
    val dutyTime8c: Int?,
    val dutyTime8s: Int?,
    val hpid: String?,
    val postCdn1: Int?,
    val postCdn2: Int?,
    @Json(name = "rnum")
    val rNum: Int?,
    val wgs84Lat: Double?,
    val wgs84Lon: Double?
)