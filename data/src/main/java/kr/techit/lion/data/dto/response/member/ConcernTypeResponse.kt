package kr.techit.lion.data.dto.response.member

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.concern.Concerns
import kr.techit.lion.domain.model.concern.ConcernType

@JsonClass(generateAdapter = true)
internal data class ConcernTypeResponse(
    val code: Int,
    val data: ConcernTypeData,
    val message: String,
) {
    fun toDomainModel(): Concerns {
        return Concerns(
            mapOf(
                ConcernType.Physical to data.isPhysical,
                ConcernType.Visual to data.isVisual,
                ConcernType.Hear to data.isHear,
                ConcernType.Child to data.isChild,
                ConcernType.Elderly to data.isElderly,
            )
        )
    }
}
