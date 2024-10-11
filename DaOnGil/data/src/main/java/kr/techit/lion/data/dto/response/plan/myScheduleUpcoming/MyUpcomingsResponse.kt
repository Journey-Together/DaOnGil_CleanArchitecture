package kr.techit.lion.data.dto.response.plan.myScheduleUpcoming

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.schedule.MyUpcomingScheduleInfo
import kr.techit.lion.domain.model.schedule.MyUpcomingSchedules

@JsonClass(generateAdapter = true)
internal data class MyUpcomingsResponse(
    val code: Int,
    val message: String,
    val data: Data,
) {
    fun toDomainModel(): MyUpcomingSchedules {
        return MyUpcomingSchedules(
            myUpcomingScheduleList = this.data.planResList.map {
                MyUpcomingScheduleInfo(
                    planId = it.planId,
                    title = it.title,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    imageUrl = it.imageUrl ?: "",
                    remainDate = it.remainDate
                )
            },
            last = data.last
        )
    }
}