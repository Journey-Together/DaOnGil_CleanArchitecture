package kr.techit.lion.data.dto.response.placereviewlist

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
internal data class MyPlaceReview(
    val content: String,
    val date: LocalDate,
    val grade: Float,
    val imageList: List<String>,
    val nickname: String,
    val profileImg: String,
    val reviewId: Int,
    val myReview: Boolean
)