package kr.techit.lion.data.dto.response.detailplace

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
internal data class ReviewRes (
    val reviewId: Long,
    val nickname : String,
    val profileImg : String,
    val content : String,
    val reviewImgs : List<String>?,
    val grade : Float,
    val date : LocalDate,
    val myReview : Boolean
)