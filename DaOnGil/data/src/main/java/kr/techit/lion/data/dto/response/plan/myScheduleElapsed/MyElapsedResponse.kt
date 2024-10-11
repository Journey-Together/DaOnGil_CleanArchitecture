package kr.techit.lion.data.dto.response.plan.myScheduleElapsed

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.schedule.MyElapsedScheduleInfo
import kr.techit.lion.domain.model.schedule.MyElapsedSchedules

@JsonClass(generateAdapter = true)
internal data class MyElapsedResponse(
    val code: Int,
    val message: String,
    val data: Data,
) {
    fun toDomainModel(): MyElapsedSchedules {
        return MyElapsedSchedules(
            myElapsedScheduleList = data.planResList.map {
                MyElapsedScheduleInfo(
                    planId = it.planId,
                    title = it.title,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    imageUrl = it.imageUrl ?: "",
                    hasReview = it.hasReview
                )
            },
            last = data.last
        )
    }
}
