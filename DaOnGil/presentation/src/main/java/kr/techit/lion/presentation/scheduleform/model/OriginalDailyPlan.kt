package kr.techit.lion.presentation.scheduleform.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OriginalDailyPlan(
    val dailyPlanDate: String,
    val schedulePlaces : List<OriginalSchedulePlace>
): Parcelable
