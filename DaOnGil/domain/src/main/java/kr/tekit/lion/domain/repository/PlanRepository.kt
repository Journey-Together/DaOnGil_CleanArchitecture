package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.model.schedule.MyElapsedSchedules
import kr.tekit.lion.domain.model.schedule.MyUpcomingSchedules
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.MyMainSchedule
import kr.tekit.lion.domain.model.OpenPlan
import kr.tekit.lion.domain.model.ScheduleDetailInfo
import kr.tekit.lion.domain.model.ScheduleDetailReview

interface PlanRepository {
    suspend fun getMyUpcomingScheduleList(page: Int): Result<MyUpcomingSchedules>

    suspend fun getMyElapsedScheduleList(page: Int): Result<MyElapsedSchedules>

    suspend fun getMyMainSchedule(): Result<List<MyMainSchedule?>?>

    suspend fun getOpenPlanList(size: Int, page: Int): Result<OpenPlan>

    suspend fun getDetailScheduleInfo(planId: Long): ScheduleDetailInfo

    suspend fun getDetailScheduleInfoGuest(planId: Long): ScheduleDetailInfo

    suspend fun getDetailScheduleReview(planId: Long): ScheduleDetailReview

}