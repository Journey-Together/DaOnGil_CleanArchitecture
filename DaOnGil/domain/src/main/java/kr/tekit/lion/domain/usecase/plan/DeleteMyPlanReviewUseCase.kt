package kr.tekit.lion.domain.usecase.plan

import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.ScheduleDetailReview
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.BaseUseCase
import kr.tekit.lion.domain.usecase.base.Result
import javax.inject.Inject

class DeleteMyPlanReviewUseCase @Inject constructor(
    private val planRepository: PlanRepository
): BaseUseCase() {
    suspend operator fun invoke(reviewId: Long, planId: Long): Result<ScheduleDetailReview> = execute {
        planRepository.deleteMyPlanReview(reviewId)
        planRepository.getDetailScheduleReview(planId)
    }
}