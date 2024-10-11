package kr.techit.lion.data.repository

import kr.techit.lion.data.datasource.ReportDataSource
import kr.techit.lion.data.dto.request.toRequestBody
import kr.techit.lion.domain.exception.Result
import kr.techit.lion.domain.model.ReportReview
import kr.techit.lion.domain.repository.ReportRepository
import javax.inject.Inject

internal class ReportRepositoryImpl @Inject constructor(
    private val reportDataSource: ReportDataSource
) : ReportRepository {

    override suspend fun reportReview(reviewType: String, request: ReportReview): Result<Unit> {
        return reportDataSource.reportReview(reviewType, request.toRequestBody())
    }
}