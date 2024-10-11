package kr.techit.lion.data.service

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ReportService {
    @POST("report")
    suspend fun reportReview(
        @Query("reviewType") reviewType: String,
        @Body requestBody: RequestBody
    )
}