package kr.tekit.lion.domain.model.schedule

data class ModifiedScheduleReview(
    val grade: Float?,
    val content: String?,
    val deleteImgUrls: List<String>?
)
