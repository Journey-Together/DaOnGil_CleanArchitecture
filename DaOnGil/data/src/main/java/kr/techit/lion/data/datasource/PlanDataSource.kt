package kr.techit.lion.data.datasource

import kr.techit.lion.data.common.execute
import kr.techit.lion.data.service.PlanService
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.MyMainSchedule
import kr.techit.lion.domain.model.OpenPlan
import kr.techit.lion.domain.model.schedule.BriefScheduleInfo
import kr.techit.lion.domain.model.ScheduleDetailInfo
import kr.techit.lion.domain.model.ScheduleDetailReview
import kr.techit.lion.domain.model.schedule.MyElapsedSchedules
import kr.techit.lion.domain.model.schedule.MyUpcomingSchedules
import kr.techit.lion.domain.model.scheduleform.PlaceSearchResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

internal class PlanDataSource @Inject constructor(
    private val planService: PlanService
) {
    companion object {
        private const val PAGE_SIZE = 10
        private const val PLACE_SEARCH_PAGE_SIZE = 20
    }

    suspend fun getMyUpcomingScheduleList(page: Int): Result<MyUpcomingSchedules> = execute {
        planService.getMyUpcomingScheduleList(PAGE_SIZE, page).toDomainModel()
    }

    suspend fun getMyElapsedScheduleList(page: Int): Result<MyElapsedSchedules> = execute {
        planService.getMyElapsedScheduleList(PAGE_SIZE, page).toDomainModel()
    }

    suspend fun getPlaceSearchResult(word: String, page: Int): Result<PlaceSearchResult> = execute {
        planService.getPlaceSearchResults(word, page, PLACE_SEARCH_PAGE_SIZE).toDomainModel()
    }

    suspend fun addNewPlan(request: RequestBody): Result<Unit> = execute {
        planService.addNewPlan(request)
    }

    suspend fun modifySchedule(planId: Long, request: RequestBody): Result<Unit> = execute {
        planService.modifySchedule(planId, request)
    }

    suspend fun getMyMainSchedule(): Result<List<MyMainSchedule?>?> = execute {
        planService.getMyMainSchedule().toDomainModel()
    }

    suspend fun getOpenPlanList(size: Int, page: Int): Result<OpenPlan> = execute {
        planService.getOpenPlanList(size, page).toDomainModel()
    }

    suspend fun getBriefScheduleInfo(planId: Long): Result<BriefScheduleInfo> = execute {
        planService.getBriefScheduleInfo(planId).toDomainModel()
    }

    suspend fun addNewScheduleReview(
        planId: Long,
        scheduleReview: RequestBody,
        images: List<MultipartBody.Part>?
    ): Result<Unit> = execute {
        if (images.isNullOrEmpty()) {
            planService.addNewScheduleReviewTextOnly(planId, scheduleReview)
        } else {
            planService.addNewScheduleReview(planId, scheduleReview, images)
        }
    }

    suspend fun modifyNewScheduleReview(
        planId: Long,
        scheduleReview: RequestBody,
        images: List<MultipartBody.Part>?
    ): Result<Unit> = execute {
        if (images.isNullOrEmpty()) {
            planService.modifyScheduleReviewTextOnly(planId, scheduleReview)
        } else {
            planService.modifyScheduleReview(planId, scheduleReview, images)
        }
    }

    suspend fun getDetailScheduleInfo(planId: Long): ScheduleDetailInfo {
        return planService.getDetailScheduleInfo(planId).toDomainModel()
    }

    suspend fun getDetailScheduleInfoGuest(planId: Long): ScheduleDetailInfo {
        return planService.getDetailScheduleInfoGuest(planId).toDomainModel()
    }

    suspend fun getDetailScheduleReview(planId: Long): ScheduleDetailReview {
        return planService.getDetailScheduleReview(planId).toDomainModel()
    }

    suspend fun getDetailScheduleReviewGuest(planId: Long): ScheduleDetailReview {
        return planService.getDetailScheduleReviewGuest(planId).toDomainModel()
    }

    suspend fun deleteMyPlanReview(reviewId: Long) {
        planService.deleteMyPlanReview(reviewId)
    }

    suspend fun updateMyPlanPublic(planId: Long) {
        planService.updateMyPlanPublic(planId)
    }

    suspend fun deleteMyPlanSchedule(planId: Long): Result<Unit> = execute {
        planService.deleteMyPlanSchedule(planId)
    }


}