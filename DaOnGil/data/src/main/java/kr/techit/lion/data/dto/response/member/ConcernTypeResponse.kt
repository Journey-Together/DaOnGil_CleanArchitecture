package kr.techit.lion.data.dto.response.member

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.ConcernType

@JsonClass(generateAdapter = true)
internal data class ConcernTypeResponse(
    val code: Int,
    val data: ConcernTypeData,
    val message: String
) {
    fun toDomainModel(): ConcernType {
        return ConcernType(
            isPhysical = data.isPhysical,
            isHear = data.isHear,
            isVisual = data.isVisual,
            isElderly = data.isElderly,
            isChild = data.isChild
        )
    }
}