package kr.techit.lion.domain.usecase.plan

import kr.techit.lion.domain.model.ScheduleDetail
import kr.techit.lion.domain.model.ScheduleDetailInfo
import kr.techit.lion.domain.model.ScheduleDetailReview
import kr.techit.lion.domain.repository.BookmarkRepository
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.domain.usecase.base.BaseUseCase
import kr.techit.lion.domain.usecase.base.Result
import javax.inject.Inject

class GetScheduleDetailUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val bookmarkRepository: BookmarkRepository
): BaseUseCase() {
    suspend operator fun invoke(planId: Long): Result<ScheduleDetail> = execute {

        val scheduleInfo = planRepository.getDetailScheduleInfo(planId)
        val reviewInfo = planRepository.getDetailScheduleReview(planId)
        val bookmark = bookmarkRepository.getPlanDetailBookmark(planId).state
        combineScheduleDetail(scheduleInfo, reviewInfo, bookmark)
    }
}


private fun combineScheduleDetail(
    info: ScheduleDetailInfo,
    review: ScheduleDetailReview,
    bookmark: Boolean
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
        isBookmark = bookmark,
        isReport = review.isReport
    )
}