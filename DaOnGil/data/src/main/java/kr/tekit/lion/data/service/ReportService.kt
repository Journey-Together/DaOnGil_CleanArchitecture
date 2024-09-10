package kr.tekit.lion.data.service

import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

internal interface ReportService {
    @Multipart
    @POST("report/{reviewType}")
    suspend fun reportReview(
        @Path("reviewType") reviewType: String,
        @Part("reportReviewReq") reportReviewReq: RequestBody
    )
}