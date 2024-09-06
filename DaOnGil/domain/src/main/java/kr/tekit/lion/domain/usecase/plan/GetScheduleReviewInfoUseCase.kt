package kr.tekit.lion.domain.usecase.plan

import kr.tekit.lion.domain.model.schedule.ScheduleReviewInfo
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.BaseUseCase
import kr.tekit.lion.domain.usecase.base.Result
import javax.inject.Inject

class GetScheduleReviewInfoUseCase @Inject constructor(
    private val planRepository: PlanRepository
) : BaseUseCase() {
    suspend operator fun invoke(planId: Long): Result<ScheduleReviewInfo> = execute {
        val scheduleInfo = planRepository.getDetailScheduleInfo(planId)
        val detailReview = planRepository.getDetailScheduleReview(planId)

        ScheduleReviewInfo(
            title = scheduleInfo.title,
            startDate = scheduleInfo.startDate,
            endDate = scheduleInfo.endDate,
            imageUrl = scheduleInfo.images?.get(0) ?: "",
            reviewId = detailReview.reviewId ?: -1,
            content = detailReview.content ?: "",
            grade = detailReview.grade?.toFloat() ?: 0.0F,
            imageList = detailReview.imageList
        )
    }
}