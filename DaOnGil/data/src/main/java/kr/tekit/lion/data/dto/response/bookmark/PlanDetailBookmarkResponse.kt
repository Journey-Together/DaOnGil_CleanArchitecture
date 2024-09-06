package kr.tekit.lion.data.dto.response.bookmark


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.PlanDetailBookmark

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