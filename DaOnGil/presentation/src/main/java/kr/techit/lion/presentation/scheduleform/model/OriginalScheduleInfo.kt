package kr.techit.lion.presentation.scheduleform.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kr.techit.lion.domain.model.ScheduleDetail

@Parcelize
data class OriginalScheduleInfo(
    val planId: Long,
    val title : String,
    val startDate : String,
    val endDate : String,
    val dailyPlans: List<OriginalDailyPlan>,
): Parcelable


fun ScheduleDetail.toOriginalScheduleInfo(planId: Long) :OriginalScheduleInfo {
    return OriginalScheduleInfo(
        planId,
        title = this.title,
        startDate = this.startDate,
        endDate = this.endDate,
        dailyPlans = this.dailyPlans.map {
            OriginalDailyPlan(
                dailyPlanDate = it.dailyPlanDate,
                schedulePlaces = it.schedulePlaces.map {
                    OriginalSchedulePlace(
                        placeId = it.placeId,
                        name = it.name,
                        category = it.category,
                        imageUrl = it.imageUrl,
                        disability = it.disability
                    )
                }
            )
        }
    )
}