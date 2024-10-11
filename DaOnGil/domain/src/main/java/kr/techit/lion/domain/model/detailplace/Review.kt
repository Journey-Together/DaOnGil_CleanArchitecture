package kr.techit.lion.domain.model.detailplace

import java.time.LocalDate

data class Review (
    val reviewId: Long,
    val nickname : String,
    val profileImg : String,
    val content : String,
    val reviewImgs : List<String>?,
    val grade : Float,
    val date : LocalDate,
    val myReview : Boolean
)