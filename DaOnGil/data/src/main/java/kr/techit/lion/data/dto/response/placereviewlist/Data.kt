package kr.techit.lion.data.dto.response.placereviewlist

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    @Json(name = "myplaceReviewList")
    val myPlaceReviewList: List<MyPlaceReview>,
    val placeImg: String,
    val pageNo: Int?,
    val pageSize: Int?,
    val placeAddress: String,
    val placeName: String,
    val totalPages: Int?,
    val reviewNum: Int
)