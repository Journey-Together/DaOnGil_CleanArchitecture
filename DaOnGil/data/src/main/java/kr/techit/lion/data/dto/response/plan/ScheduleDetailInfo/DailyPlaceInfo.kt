package kr.techit.lion.data.dto.response.plan.scheduleDetailInfo

import kr.techit.lion.domain.model.SchedulePlace

internal data class DailyPlaceInfo(
    val category: String,
    val imageUrl: String?,
    val disabilityCategoryList: List<Int>,
    val name: String,
    val placeId: Long
){
    fun toDomainModel(): SchedulePlace {
        return SchedulePlace(
            placeId = placeId,
            name = name,
            category = category,
            imageUrl = imageUrl ?: "",
            disability = disabilityCategoryList
        )
    }
}
