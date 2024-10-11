package kr.techit.lion.data.dto.response.plan.briefScheduleInfo

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.schedule.BriefScheduleInfo

@JsonClass(generateAdapter = true)
internal data class BriefScheduleInfoResponse(
    val code: Int,
    val message: String,
    val data: Data?
){
    fun toDomainModel() : BriefScheduleInfo {
        return BriefScheduleInfo(
            planId = this.data?.planId,
            title = this.data?.title,
            startDate = this.data?.startDate,
            endDate = this.data?.endDate,
            imageUrl = this.data?.imageUrl
        )
    }
}

