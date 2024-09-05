package kr.tekit.lion.data.dto.response.placereview

import kr.tekit.lion.domain.model.placereview.WritePlaceReview

internal data class WritePlaceReviewResponse (
    val code: Int,
    val data: String?,
    val message: String
) {
    fun toDomainModel(): WritePlaceReview {
        return WritePlaceReview(
            code = code
        )
    }
}