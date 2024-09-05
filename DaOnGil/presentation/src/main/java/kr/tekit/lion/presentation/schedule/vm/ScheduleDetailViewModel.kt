package kr.tekit.lion.presentation.schedule.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.ScheduleDetail
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.onError
import kr.tekit.lion.domain.usecase.base.onSuccess
import kr.tekit.lion.domain.usecase.plan.DeleteMyPlanReviewUseCase
import kr.tekit.lion.domain.usecase.plan.GetScheduleDetailGuestUseCase
import kr.tekit.lion.domain.usecase.plan.GetScheduleDetailUseCase
import kr.tekit.lion.domain.usecase.plan.UpdateMyPlanPublicUseCase
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class ScheduleDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val planRepository: PlanRepository,
    private val getScheduleDetailUseCase: GetScheduleDetailUseCase,
    private val getScheduleDetailGuestUseCase: GetScheduleDetailGuestUseCase,
    private val deleteMyPlanReviewUseCase: DeleteMyPlanReviewUseCase,
    private val updateMyPlanPublicUseCase: UpdateMyPlanPublicUseCase
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _scheduleDetail = MutableLiveData<ScheduleDetail>()
    val scheduleDetail: LiveData<ScheduleDetail> = _scheduleDetail

    fun getScheduleDetailInfo(planId: Long) =
        viewModelScope.launch {
            getScheduleDetailUseCase.invoke(planId).onSuccess {
                _scheduleDetail.value = it
            }
        }

    fun getScheduleDetailInfoGuest(planId: Long) =
        viewModelScope.launch {
            getScheduleDetailGuestUseCase.invoke(planId).onSuccess {
                _scheduleDetail.value = it
            }
        }

    fun deleteMyPlanReview(reviewId: Long, planId: Long) =
        viewModelScope.launch {
            deleteMyPlanReviewUseCase.invoke(reviewId, planId).onSuccess {
                _scheduleDetail.value = _scheduleDetail.value?.copy(
                    reviewId = it.reviewId,
                    content = it.content,
                    grade = it.grade,
                    reviewImages = it.imageList,
                    hasReview = it.hasReview
                )
            }
        }

    fun updateMyPlanPublic(planId: Long) =
        viewModelScope.launch {
            updateMyPlanPublicUseCase.invoke(planId).onSuccess {
                _scheduleDetail.value = _scheduleDetail.value?.copy(
                    isPublic = it.isPublic
                )
            }
        }

    fun deleteMyPlanSchedule(planId: Long) =
        viewModelScope.launch {
            planRepository.deleteMyPlanSchedule(planId).onError {
                networkErrorDelegate.handleNetworkError(it)
            }
        }

}