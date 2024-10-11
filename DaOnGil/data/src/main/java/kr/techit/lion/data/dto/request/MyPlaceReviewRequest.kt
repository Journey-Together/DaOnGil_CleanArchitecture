package kr.techit.lion.data.dto.request

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kr.techit.lion.data.dto.request.util.LocalDateAdapter
import kr.techit.lion.domain.model.UpdateMyPlaceReview
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

@JsonClass(generateAdapter = true)
internal data class MyPlaceReviewRequest(
    val grade: Float,
    val date: LocalDate,
    val content: String,
    val deleteImgUrls: List<String>
)

fun UpdateMyPlaceReview.toRequestBody(): RequestBody {
    val moshi = Moshi.Builder()
        .add(LocalDateAdapter())
        .build()

    return moshi.adapter(MyPlaceReviewRequest::class.java).toJson(
        MyPlaceReviewRequest(
            this.grade,
            this.date,
            this.content,
            this.deleteImgUrls
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}