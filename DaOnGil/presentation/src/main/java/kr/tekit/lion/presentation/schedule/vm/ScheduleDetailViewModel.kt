package kr.tekit.lion.presentation.schedule.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.onError
import kr.tekit.lion.domain.exception.onSuccess
import kr.tekit.lion.domain.model.ScheduleDetail
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.usecase.base.onError
import kr.tekit.lion.domain.usecase.base.onSuccess
import kr.tekit.lion.domain.usecase.bookmark.UpdateScheduleDetailBookmarkUseCase
import kr.tekit.lion.domain.usecase.plan.DeleteMyPlanReviewUseCase
import kr.tekit.lion.domain.usecase.plan.GetScheduleDetailGuestUseCase
import kr.tekit.lion.domain.usecase.plan.GetScheduleDetailUseCase
import kr.tekit.lion.domain.usecase.plan.UpdateMyPlanPublicUseCase
import kr.tekit.lion.presentation.delegate.NetworkErrorDelegate
import kr.tekit.lion.presentation.scheduleform.model.OriginalScheduleInfo
import kr.tekit.lion.presentation.scheduleform.model.toOriginalScheduleInfo
import kr.tekit.lion.presentation.splash.model.LogInState
import javax.inject.Inject

@HiltViewModel
class ScheduleDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val planRepository: PlanRepository,
    private val getScheduleDetailUseCase: GetScheduleDetailUseCase,
    private val getScheduleDetailGuestUseCase: GetScheduleDetailGuestUseCase,
    private val deleteMyPlanReviewUseCase: DeleteMyPlanReviewUseCase,
    private val updateMyPlanPublicUseCase: UpdateMyPlanPublicUseCase,
    private val updateScheduleDetailBookmarkUseCase: UpdateScheduleDetailBookmarkUseCase
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _scheduleDetail = MutableLiveData<ScheduleDetail>()
    val scheduleDetail: LiveData<ScheduleDetail> = _scheduleDetail

    private val _loginState = MutableStateFlow<LogInState>(LogInState.Checking)
    val loginState = _loginState.asStateFlow()

    private val _snackbarSuccessMessage = MutableLiveData<String>()
    val snackbarSuccessMessage: LiveData<String> = _snackbarSuccessMessage

    private val _deletePlanSuccess = MutableLiveData<Boolean>()
    val deletePlanSuccess: LiveData<Boolean> = _deletePlanSuccess

    private val _deleteReviewSuccess = MutableLiveData<Boolean>()
    val deleteReviewSuccess: LiveData<Boolean> = _deleteReviewSuccess

    private val _updatePublicSuccess = MutableLiveData<Boolean>()
    val updatePublicSuccess: LiveData<Boolean> = _updatePublicSuccess

    private val _updateBookmarkSuccess = MutableLiveData<Boolean>()
    val updateBookmarkSuccess: LiveData<Boolean> = _updateBookmarkSuccess

    val networkState get() = networkErrorDelegate.networkState

    init {
        checkLoginState()
    }

    fun getScheduleDetailInfo(planId: Long) =
        viewModelScope.launch {
            getScheduleDetailUseCase.invoke(planId).onSuccess {
                _scheduleDetail.value = it
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(networkErrorDelegate.handleUsecaseNetworkError(it))
            }
        }

    fun getScheduleDetailInfoGuest(planId: Long) =
        viewModelScope.launch {
            getScheduleDetailGuestUseCase.invoke(planId).onSuccess {
                _scheduleDetail.value = it
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                networkErrorDelegate.handleNetworkError(networkErrorDelegate.handleUsecaseNetworkError(it))
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
                _snackbarSuccessMessage.value = "여행 일정 후기가 삭제되었습니다"
                _deletePlanSuccess.value = true
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _deleteReviewSuccess.value = false
                networkErrorDelegate.handleNetworkError(networkErrorDelegate.handleUsecaseNetworkError(it))
            }
        }

    fun updateMyPlanPublic(planId: Long) =
        viewModelScope.launch {
            updateMyPlanPublicUseCase.invoke(planId).onSuccess {
                _scheduleDetail.value = _scheduleDetail.value?.copy(
                    isPublic = it.isPublic
                )
                _snackbarSuccessMessage.value = if (it.isPublic) {
                    "여행 일정이 공개되었습니다"
                } else {
                    "여행 일정이 비공개되었습니다"
                }
                _updatePublicSuccess.value = true
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _updatePublicSuccess.value = false
                networkErrorDelegate.handleNetworkError(networkErrorDelegate.handleUsecaseNetworkError(it))
            }
        }

    fun deleteMyPlanSchedule(planId: Long) =
        viewModelScope.launch {
            planRepository.deleteMyPlanSchedule(planId).onSuccess {
                _deletePlanSuccess.value = true
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _deletePlanSuccess.value = false
                networkErrorDelegate.handleNetworkError(it)
            }
        }

    fun updateScheduleDetailBookmark(planId: Long) =
        viewModelScope.launch {
            updateScheduleDetailBookmarkUseCase(planId).onSuccess {
                _scheduleDetail.value = _scheduleDetail.value?.copy(
                    isBookmark = it.state
                )
                _snackbarSuccessMessage.value = if (it.state) {
                    "북마크 되었습니다"
                } else {
                    "북마크가 취소되었습니다"
                }
                _updateBookmarkSuccess.value = true
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _updateBookmarkSuccess.value = false
                networkErrorDelegate.handleNetworkError(networkErrorDelegate.handleUsecaseNetworkError(it))
            }
        }

    private fun checkLoginState() =
        viewModelScope.launch {
            authRepository.loggedIn.collect { isLoggedIn ->
                if (isLoggedIn) _loginState.value = LogInState.LoggedIn
                else _loginState.value = LogInState.LoginRequired
            }
        }

    fun selectDataForModification(planId: Long): OriginalScheduleInfo? {
        val scheduleData = _scheduleDetail.value?.toOriginalScheduleInfo(planId)

        return scheduleData
    }

}