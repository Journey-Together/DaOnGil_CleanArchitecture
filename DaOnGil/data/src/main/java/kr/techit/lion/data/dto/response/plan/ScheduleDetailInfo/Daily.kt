package kr.techit.lion.data.dto.response.plan.scheduleDetailInfo

import kr.techit.lion.domain.model.DailyPlan

internal data class Daily(
    val dailyPlaceInfoList: List<DailyPlaceInfo>?,
    val date: String
){
    fun toDomainModel(): DailyPlan {
        return DailyPlan(
            dailyPlanDate = date,
            schedulePlaces = dailyPlaceInfoList?.map { it.toDomainModel() } ?: emptyList()
        )
    }
}
