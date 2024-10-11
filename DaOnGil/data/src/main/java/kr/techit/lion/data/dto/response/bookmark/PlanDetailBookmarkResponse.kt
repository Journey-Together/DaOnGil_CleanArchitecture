package kr.techit.lion.data.dto.response.bookmark


import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.PlanDetailBookmark

@JsonClass(generateAdapter = true)
internal data class PlanDetailBookmarkResponse(
    val code: Int,
    val data: Data,
    val message: String?
){
    fun toDomainModel(): PlanDetailBookmark {
        return PlanDetailBookmark(
            state = this.data.state
        )
    }
}