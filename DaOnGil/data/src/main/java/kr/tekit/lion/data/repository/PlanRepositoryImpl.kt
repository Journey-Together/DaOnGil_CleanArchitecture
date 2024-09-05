package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.PlanDataSource
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.MyMainSchedule
import kr.tekit.lion.domain.model.OpenPlan
import kr.tekit.lion.domain.model.ScheduleDetailInfo
import kr.tekit.lion.domain.model.schedule.MyElapsedSchedules
import kr.tekit.lion.domain.model.schedule.MyUpcomingSchedules
import kr.tekit.lion.domain.repository.PlanRepository
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val planDataSource: PlanDataSource
) : PlanRepository {
    override suspend fun getMyUpcomingScheduleList(page: Int): Result<MyUpcomingSchedules> {
        return planDataSource.getMyUpcomingScheduleList(page)
    }

    override suspend fun getMyElapsedScheduleList(page: Int): Result<MyElapsedSchedules> {
        return planDataSource.getMyElapsedScheduleList(page)
    }

    override suspend fun getMyMainSchedule(): Result<List<MyMainSchedule?>?> {
        return planDataSource.getMyMainSchedule()
    }

    override suspend fun getOpenPlanList(size: Int, page: Int): Result<OpenPlan> {
        return planDataSource.getOpenPlanList(size, page)
    }

    override suspend fun getDetailScheduleInfo(planId: Long): ScheduleDetailInfo {
        return planDataSource.getDetailScheduleInfo(planId)
    }

    override suspend fun getDetailScheduleInfoGuest(planId: Long): ScheduleDetailInfo {
        return planDataSource.getDetailScheduleInfoGuest(planId)
    }
}