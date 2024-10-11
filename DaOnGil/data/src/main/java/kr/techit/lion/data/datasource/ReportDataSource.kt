package kr.techit.lion.data.datasource

import kr.techit.lion.data.common.execute
import kr.techit.lion.data.service.ReportService
import okhttp3.RequestBody
import javax.inject.Inject

internal class ReportDataSource @Inject constructor(
    private val reportService: ReportService
) {
    suspend fun reportReview(reviewType: String, requestBody: RequestBody) = execute {
        reportService.reportReview(reviewType, requestBody)
    }
}