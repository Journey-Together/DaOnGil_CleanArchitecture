package kr.techit.lion.domain.usecase.plan

import kr.techit.lion.domain.model.ScheduleDetailReview
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.domain.usecase.base.BaseUseCase
import kr.techit.lion.domain.usecase.base.Result
import javax.inject.Inject

class DeleteMyPlanReviewUseCase @Inject constructor(
    private val planRepository: PlanRepository
): BaseUseCase() {
    suspend operator fun invoke(reviewId: Long, planId: Long): Result<ScheduleDetailReview> = execute {
        planRepository.deleteMyPlanReview(reviewId)
        planRepository.getDetailScheduleReview(planId)
    }
}