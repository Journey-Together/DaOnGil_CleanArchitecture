package kr.techit.lion.data.dto.request

import com.squareup.moshi.Json

import kr.techit.lion.domain.model.scheduleform.NewPlan
import kr.techit.lion.data.dto.request.util.AdapterProvider.Companion.JsonAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal data class NewPlanRequest(
    val title: String,
    val startDate: String,
    val endDate: String,
    @Json(name = "dailyplace")
    val dailyPlace: List<DailyPlaceRequest>,
)

fun NewPlan.toRequestBody(): RequestBody {
    return JsonAdapter(NewPlanRequest::class.java).toJson(
        NewPlanRequest(
            this.title,
            this.startDate,
            this.endDate,
            this.dailyPlace.map {
                DailyPlaceRequest(
                    it.date,
                    it.places
                )
            }
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
}