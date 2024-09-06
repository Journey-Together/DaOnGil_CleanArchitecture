package kr.tekit.lion.domain.usecase.plan

import kr.tekit.lion.domain.model.ScheduleDetailInfo
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.BaseUseCase
import kr.tekit.lion.domain.usecase.base.Result
import javax.inject.Inject

class UpdateMyPlanPublicUseCase @Inject constructor(
    private val planRepository: PlanRepository
): BaseUseCase() {
    suspend operator fun invoke(planId: Long): Result<ScheduleDetailInfo> = execute {
        planRepository.updateMyPlanPublic(planId)
        planRepository.getDetailScheduleInfo(planId)
    }
}