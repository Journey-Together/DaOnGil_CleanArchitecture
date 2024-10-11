package kr.techit.lion.data.dto.response.myreview

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
internal data class MyPlaceReviewDto(
    val content: String,
    val date: LocalDate,
    val grade: Float,
    val images: List<String>,
    val isReport: Boolean?,
    val name: String,
    val placeId: Long,
    val reviewId: Long
)