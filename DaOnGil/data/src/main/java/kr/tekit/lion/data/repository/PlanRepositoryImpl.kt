package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.PlanDataSource
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.schedule.MyElapsedSchedules
import kr.tekit.lion.domain.model.schedule.MyUpcomingSchedules
import kr.tekit.lion.domain.model.scheduleform.PlaceSearchResult
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

    override suspend fun getPlaceSearchResult(word: String, page: Int): Result<PlaceSearchResult> {
        return planDataSource.getPlaceSearchResult(word, page)
    }
}