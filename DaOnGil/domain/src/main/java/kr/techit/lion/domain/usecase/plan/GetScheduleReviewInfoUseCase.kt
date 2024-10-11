package kr.techit.lion.domain.usecase.plan

import kr.techit.lion.domain.model.schedule.ScheduleReviewInfo
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.domain.usecase.base.BaseUseCase
import kr.techit.lion.domain.usecase.base.Result
import javax.inject.Inject

/* 임시 UseCase - 삭제할 것 */
class GetScheduleReviewInfoUseCase @Inject constructor(
    private val planRepository: PlanRepository
) : BaseUseCase() {
    suspend operator fun invoke(planId: Long): Result<ScheduleReviewInfo> = execute {
        val detailReview = planRepository.getDetailScheduleReview(planId)

        ScheduleReviewInfo(
            title = "",
            startDate = "",
            endDate = "",
            imageUrl = "",
            reviewId = detailReview.reviewId ?: -1,
            content = detailReview.content ?: "",
            grade = detailReview.grade?.toFloat() ?: 0.0F,
            imageList = detailReview.imageList
        )
    }
}