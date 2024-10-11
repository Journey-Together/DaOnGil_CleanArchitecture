package kr.techit.lion.data.dto.response.searchplace.auto_complete_keyword

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "keyword")
    val keyword: String,
    @Json(name = "placeId")
    val placeId: Long
)