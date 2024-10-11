package kr.techit.lion.data.dto.request

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kr.techit.lion.data.dto.request.util.LocalDateAdapter
import kr.techit.lion.domain.model.placereview.NewReviewData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class NewReviewDataRequest(
    val date : LocalDate,
    val grade : Float,
    val content : String
)

fun NewReviewData.toRequestBody(): RequestBody {
    val moshi = Moshi.Builder()
        .add(LocalDateAdapter())
        .build()

    return moshi.adapter(NewReviewDataRequest::class.java).toJson(
        NewReviewDataRequest(
            this.date,
            this.grade,
            this.content
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}