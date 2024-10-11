package kr.techit.lion.presentation.report.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.techit.lion.domain.exception.onError
import kr.techit.lion.domain.exception.onSuccess
import kr.techit.lion.domain.model.ReportReview
import kr.techit.lion.domain.repository.ReportRepository
import kr.techit.lion.presentation.delegate.NetworkErrorDelegate
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    @Inject
    lateinit var networkErrorDelegate: NetworkErrorDelegate

    private val _reviewType = MutableLiveData<String>()
    private val _reviewId = MutableLiveData<Long>()
    private val _selectedReason = MutableLiveData("도용")
    private val _detailedReason = MutableLiveData<String>()

    val networkState get() = networkErrorDelegate.networkState

    init {
        val reviewType = savedStateHandle.get<String>("reviewType") ?: ""
        val reviewId = savedStateHandle.get<Long>("reviewId") ?: 0L

        _reviewType.value = reviewType
        _reviewId.value = reviewId
    }

    fun setSelectedReason(reason: String) {
        _selectedReason.value = reason
    }

    fun setDetailedReason(reason: String) {
        _detailedReason.value = reason
    }

    fun submitReportReview() {
        val reviewType = _reviewType.value

        val reportReview = ReportReview(
            _reviewId.value ?: 0L,
            _selectedReason.value ?: "도용",
            _detailedReason.value
        )

        viewModelScope.launch {
            if (reviewType != null) {
                val result = reportRepository.reportReview(reviewType, reportReview)

                result.onSuccess {
                    networkErrorDelegate.handleNetworkSuccess()
                }.onError {
                    networkErrorDelegate.handleNetworkError(it)
                }
            }
        }
    }
}