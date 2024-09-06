package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.schedule.MyElapsedSchedules
import kr.tekit.lion.domain.model.schedule.MyUpcomingSchedules
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.scheduleform.NewPlan
import kr.tekit.lion.domain.model.scheduleform.PlaceSearchResult
import kr.tekit.lion.domain.model.MyMainSchedule
import kr.tekit.lion.domain.model.OpenPlan
import kr.tekit.lion.domain.model.schedule.BriefScheduleInfo
import kr.tekit.lion.domain.model.schedule.NewScheduleReview
import kr.tekit.lion.domain.model.schedule.ReviewImg

interface PlanRepository {
    suspend fun getMyUpcomingScheduleList(page: Int): Result<MyUpcomingSchedules>

    suspend fun getMyElapsedScheduleList(page: Int): Result<MyElapsedSchedules>

    suspend fun getPlaceSearchResult(word: String, page: Int): Result<PlaceSearchResult>

    suspend fun addNewPlan(request: NewPlan): Result<Unit>

    suspend fun getMyMainSchedule(): Result<List<MyMainSchedule?>?>

    suspend fun getOpenPlanList(size: Int, page: Int): Result<OpenPlan>

    suspend fun getBriefScheduleInfo(planId: Long): Result<BriefScheduleInfo>

    suspend fun addNewScheduleReview(
        planId: Long,
        scheduleReview: NewScheduleReview,
        images: List<ReviewImg>
    ): Result<Unit>
}