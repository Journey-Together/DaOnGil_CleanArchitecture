package kr.tekit.lion.domain.repository

import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.ReportReview

interface ReportRepository {
    suspend fun reportReview(reviewType: String, request: ReportReview): Result<Unit>
}