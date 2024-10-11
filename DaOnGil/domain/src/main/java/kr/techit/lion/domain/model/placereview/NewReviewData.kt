package kr.techit.lion.domain.model.placereview

import java.time.LocalDate

data class NewReviewData(
    val date : LocalDate,
    val grade : Float,
    val content : String
)