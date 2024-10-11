package kr.techit.lion.domain.repository

import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.ReportReview

interface ReportRepository {
    suspend fun reportReview(reviewType: String, request: ReportReview): Result<Unit>
}