package kr.techit.lion.data.dto.response.aed

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Item(
    val buildAddress: String?,
    val buildPlace: String?,
    val clerkTel: String?,
    val friEndTme: Int?,
    val friSttTme: String?,
    val holEndTme: Int?,
    val holSttTme: String?,
    val manager: String?,
    val managerTel: String?,
    val mfg: String?,
    val model: String?,
    val monEndTme: Int?,
    val monSttTme: String?,
    val org: String?,
    val satEndTme: Int?,
    val satSttTme: String?,
    val sunEndTme: Int?,
    val sunFifYon: String?,
    val sunFrtYon: String?,
    val sunFurYon: String?,
    val sunScdYon: String?,
    val sunSttTme: String?,
    val sunThiYon: String?,
    val thuEndTme: Int?,
    val thuSttTme: String?,
    val tueEndTme: Int?,
    val tueSttTme: String?,
    val wedEndTme: Int?,
    val wedSttTme: String?,
    val wgs84Lat: Double?,
    val wgs84Lon: Double?,
    val zipcode1: Int?,
    val zipcode2: Int?,
    val rnum: Int?
)
