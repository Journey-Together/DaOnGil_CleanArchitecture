package kr.techit.lion.data.dto.response.emergency.realtime

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.EmergencyRealtimeInfo

@JsonClass(generateAdapter = true)
internal data class EmergencyRealtimeResponse(
    val response: Response
) {
    fun toDomainModel(): List<EmergencyRealtimeInfo> {
        return this.response.body.items.item.map { item ->
            EmergencyRealtimeInfo(
                hospitalId = item.hpid,
                emergencyCount = item.hvec,
                emergencyKid = item.hv10,
                emergencyKidCount = item.hv28,
                emergencyAllCount = item.hvs01,
                emergencyKidAllCount = item.hvs02,
                lastUpdateDate = formatDate(item.hvidate.toString()),
            )
        }
    }
    fun formatDate(input: String): String {
        if (input.length != 14) return "-"

        try {
            val year = input.substring(0, 4)
            val month = input.substring(4, 6)
            val day = input.substring(6, 8)
            val hour = input.substring(8, 10)
            val minute = input.substring(10, 12)
            val second = input.substring(12, 14)

            return "$year.$month.$day $hour:$minute:$second"
        } catch (e: Exception) {
            return "-"
        }
    }
}