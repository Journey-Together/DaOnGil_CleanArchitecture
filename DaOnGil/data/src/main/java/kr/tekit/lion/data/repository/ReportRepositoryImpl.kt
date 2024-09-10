package kr.tekit.lion.data.repository

import kr.tekit.lion.data.datasource.ReportDataSource
import kr.tekit.lion.data.dto.request.toRequestBody
import kr.tekit.lion.domain.exception.Result
import kr.tekit.lion.domain.model.ReportReview
import kr.tekit.lion.domain.repository.ReportRepository
import javax.inject.Inject

internal class ReportRepositoryImpl @Inject constructor(
    private val reportDataSource: ReportDataSource
) : ReportRepository {

    override suspend fun reportReview(reviewType: String, reportReviewReq: ReportReview): Result<Unit> {
        return reportDataSource.reportReview(reviewType, reportReviewReq.toRequestBody())
    }
}