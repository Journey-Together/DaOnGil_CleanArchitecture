package kr.tekit.lion.daongil.data.dto.remote.response.bookmark

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.PlanBookmark

@JsonClass(generateAdapter = true)
data class PlanBookmarkResponse(
    val code: Int,
    val data: List<PlanBookmarkData>,
    val message: String
) {
    fun toDomainModel(): List<PlanBookmark> {
        return data.map { planBookmarkData ->
            PlanBookmark(
                image = planBookmarkData.image,
                name = planBookmarkData.name,
                planId = planBookmarkData.planId,
                profileImg = planBookmarkData.profileImg,
                title = planBookmarkData.title
            )
        }
    }
}