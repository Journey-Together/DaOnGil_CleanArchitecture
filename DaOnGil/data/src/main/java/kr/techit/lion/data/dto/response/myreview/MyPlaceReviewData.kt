package kr.techit.lion.data.dto.response.myreview

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class MyPlaceReviewData(
    val myPlaceReviewDtoList: List<MyPlaceReviewDto>,
    val pageNo: Int,
    val pageSize: Int,
    val reviewNum: Long,
    val totalPages: Int
)