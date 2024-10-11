package kr.techit.lion.domain.usecase.bookmark

import kr.techit.lion.domain.model.PlanDetailBookmark
import kr.techit.lion.domain.repository.BookmarkRepository
import kr.techit.lion.domain.usecase.base.BaseUseCase
import kr.techit.lion.domain.usecase.base.Result
import javax.inject.Inject

class UpdateScheduleDetailBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
): BaseUseCase(){
    suspend operator fun invoke(planId: Long): Result<PlanDetailBookmark> = execute {
        bookmarkRepository.updatePlanBookmark(planId)
        bookmarkRepository.getPlanDetailBookmark(planId)
    }
}