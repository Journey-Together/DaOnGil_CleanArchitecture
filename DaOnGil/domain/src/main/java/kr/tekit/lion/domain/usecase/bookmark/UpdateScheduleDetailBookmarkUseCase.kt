package kr.tekit.lion.domain.usecase.bookmark

import kr.tekit.lion.domain.model.PlanDetailBookmark
import kr.tekit.lion.domain.model.ScheduleDetailInfo
import kr.tekit.lion.domain.repository.BookmarkRepository
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.BaseUseCase
import kr.tekit.lion.domain.usecase.base.Result
import javax.inject.Inject

class UpdateScheduleDetailBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
): BaseUseCase(){
    suspend operator fun invoke(planId: Long): Result<PlanDetailBookmark> = execute {
        bookmarkRepository.updatePlanBookmark(planId)
        bookmarkRepository.getPlanDetailBookmark(planId)
    }
}