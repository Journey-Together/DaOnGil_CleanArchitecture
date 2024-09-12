package kr.tekit.lion.presentation.schedule.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.tekit.lion.domain.exception.AuthenticationError
import kr.tekit.lion.domain.exception.AuthorizationError
import kr.tekit.lion.domain.exception.BadRequestError
import kr.tekit.lion.domain.exception.ConnectError
import kr.tekit.lion.domain.exception.NetworkError
import kr.tekit.lion.domain.exception.NotFoundError
import kr.tekit.lion.domain.exception.ServerError
import kr.tekit.lion.domain.exception.TimeoutError
import kr.tekit.lion.domain.exception.UnknownError
import kr.tekit.lion.domain.exception.UnknownHostError
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
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.scheduleform.model.OriginalScheduleInfo
import kr.tekit.lion.presentation.scheduleform.model.toOriginalScheduleInfo
import kr.tekit.lion.presentation.splash.model.LogInState
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

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
                _snackbarMessage.value = "여행 일정 후기가 삭제되었습니다"
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _snackbarMessage.value = "여행 일정 후기가 삭제 처리되지 않았습니다"
                networkErrorDelegate.handleNetworkError(networkErrorDelegate.handleUsecaseNetworkError(it))
            }
        }

    fun updateMyPlanPublic(planId: Long) =
        viewModelScope.launch {
            updateMyPlanPublicUseCase.invoke(planId).onSuccess {
                _scheduleDetail.value = _scheduleDetail.value?.copy(
                    isPublic = it.isPublic
                )
                _snackbarMessage.value = if (it.isPublic) {
                    "여행 일정이 공개되었습니다"
                } else {
                    "여행 일정이 비공개되었습니다"
                }
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _snackbarMessage.value = "여행 일정 공개/비공개가 처리되지 않았습니다"
                networkErrorDelegate.handleNetworkError(networkErrorDelegate.handleUsecaseNetworkError(it))
            }
        }

    fun deleteMyPlanSchedule(planId: Long) =
        viewModelScope.launch {
            planRepository.deleteMyPlanSchedule(planId).onSuccess {
                _deleteSuccess.value = true
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _deleteSuccess.value = false
                _snackbarMessage.value = "여행 일정 삭제가 처리되지 않았습니다"
                networkErrorDelegate.handleNetworkError(it)
            }
        }

    fun updateScheduleDetailBookmark(planId: Long) =
        viewModelScope.launch {
            updateScheduleDetailBookmarkUseCase(planId).onSuccess {
                _scheduleDetail.value = _scheduleDetail.value?.copy(
                    isBookmark = it.state
                )
                _snackbarMessage.value = if (it.state) {
                    "북마크 되었습니다"
                } else {
                    "북마크가 취소되었습니다"
                }
                networkErrorDelegate.handleNetworkSuccess()
            }.onError {
                _snackbarMessage.value = "북마크가 처리되지 않았습니다"
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