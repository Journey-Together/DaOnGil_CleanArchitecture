package kr.techit.lion.data.dto.response.plan.myMainSchedule

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.MyMainSchedule

@JsonClass(generateAdapter = true)
internal data class MyMainScheduleResponse(
    val code: Int,
    val data: List<Data?>?,
    val message: String
) {
    fun toDomainModel(): List<MyMainSchedule?>? {
        return data?.map{
            MyMainSchedule(
                endDate = it?.endDate,
                hasReview = it?.hasReview,
                imageUrl = it?.imageUrl,
                planId = it?.planId,
                remainDate = it?.remainDate,
                startDate = it?.startDate,
                title = it?.title,
            )
        }
    }
}
