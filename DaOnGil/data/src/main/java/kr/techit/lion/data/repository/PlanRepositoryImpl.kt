package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.PlanDataSource
import kr.techit.lion.data.dto.request.toMultipartBodyList
import kr.techit.lion.data.dto.request.toRequestBody
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.MyMainSchedule
import kr.techit.lion.domain.model.OpenPlan
import kr.techit.lion.domain.model.schedule.BriefScheduleInfo
import kr.techit.lion.domain.model.ScheduleDetailInfo
import kr.techit.lion.domain.model.ScheduleDetailReview
import kr.techit.lion.domain.model.schedule.ModifiedScheduleReview
import kr.techit.lion.domain.model.schedule.MyElapsedSchedules
import kr.techit.lion.domain.model.schedule.MyUpcomingSchedules
import kr.techit.lion.domain.model.schedule.NewScheduleReview
import kr.techit.lion.domain.model.schedule.ReviewImage
import kr.techit.lion.domain.model.schedule.ReviewImg
import kr.techit.lion.domain.model.scheduleform.NewPlan
import kr.techit.lion.domain.model.scheduleform.PlaceSearchResult
import kr.techit.lion.domain.repository.PlanRepository
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

    override suspend fun addNewPlan(request: NewPlan) : Result<Unit>{
        return planDataSource.addNewPlan(request.toRequestBody())
    }

    override suspend fun modifySchedule(planId: Long, request: NewPlan): Result<Unit> {
        return planDataSource.modifySchedule(planId, request.toRequestBody())
    }

    override suspend fun getMyMainSchedule(): Result<List<MyMainSchedule?>?> {
        return planDataSource.getMyMainSchedule()
    }

    override suspend fun getOpenPlanList(size: Int, page: Int): Result<OpenPlan> {
        return planDataSource.getOpenPlanList(size, page)
    }

    override suspend fun getBriefScheduleInfo(planId: Long): Result<BriefScheduleInfo> {
        return planDataSource.getBriefScheduleInfo(planId)
    }

    override suspend fun addNewScheduleReview(
        planId: Long,
        scheduleReview: NewScheduleReview,
        images: List<ReviewImg>
    ): Result<Unit> {
        return planDataSource.addNewScheduleReview(
            planId,
            scheduleReview.toRequestBody(),
            images.toMultipartBodyList()
        )
    }

    override suspend fun modifyScheduleReview(
        reviewId: Long,
        scheduleReview: ModifiedScheduleReview,
        images: List<ReviewImage>?
    ): Result<Unit> {
        return planDataSource.modifyNewScheduleReview(
            reviewId,
            scheduleReview.toRequestBody(),
            images?.toMultipartBodyList()
        )
    }

    override suspend fun getDetailScheduleInfo(planId: Long): ScheduleDetailInfo {
        return planDataSource.getDetailScheduleInfo(planId)
    }

    override suspend fun getDetailScheduleInfoGuest(planId: Long): ScheduleDetailInfo {
        return planDataSource.getDetailScheduleInfoGuest(planId)
    }

    override suspend fun getDetailScheduleReview(planId: Long): ScheduleDetailReview {
        return planDataSource.getDetailScheduleReview(planId)
    }

    override suspend fun getDetailScheduleReviewGuest(planId: Long): ScheduleDetailReview {
        return planDataSource.getDetailScheduleReviewGuest(planId)
    }

    override suspend fun deleteMyPlanReview(reviewId: Long) {
        return planDataSource.deleteMyPlanReview(reviewId)
    }

    override suspend fun updateMyPlanPublic(planId: Long) {
        return planDataSource.updateMyPlanPublic(planId)
    }

    override suspend fun deleteMyPlanSchedule(planId: Long): Result<Unit> {
        return planDataSource.deleteMyPlanSchedule(planId)
    }
}