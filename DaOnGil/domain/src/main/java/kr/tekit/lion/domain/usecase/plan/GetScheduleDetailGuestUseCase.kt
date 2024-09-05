package kr.tekit.lion.domain.usecase.plan

import kr.tekit.lion.domain.model.ScheduleDetail
import kr.tekit.lion.domain.model.ScheduleDetailInfo
import kr.tekit.lion.domain.model.ScheduleDetailReview
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.BaseUseCase
import kr.tekit.lion.domain.usecase.base.Result
import javax.inject.Inject

class GetScheduleDetailGuestUseCase @Inject constructor(
    private val planRepository: PlanRepository
): BaseUseCase() {
    suspend operator fun invoke(planId: Long): Result<ScheduleDetail> = execute {

        val scheduleInfo = planRepository.getDetailScheduleInfoGuest(planId)
        val reviewInfo = planRepository.getDetailScheduleReviewGuest(planId)

        combineScheduleDetail(scheduleInfo, reviewInfo)
    }
}


private fun combineScheduleDetail(
    info: ScheduleDetailInfo,
    review: ScheduleDetailReview
): ScheduleDetail {
    return ScheduleDetail(
        title = info.title,
        startDate = info.startDate,
        endDate = info.endDate,
        remainDate = info.remainDate,
        isPublic = info.isPublic,
        isWriter = info.isWriter,
        nickname = info.nickname,
        images = info.images,
        dailyPlans = info.dailyPlans,
        writerId = info.writerId,
        reviewId = review.reviewId,
        content = review.content,
        grade = review.grade,
        reviewImages = review.imageList,
        hasReview = review.hasReview,
        profileUrl = review.profileUrl,
        isBookmark = false
    )
}