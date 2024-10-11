package kr.techit.lion.data.dto.response.emergency.message

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.EmergencyMessageInfo

@JsonClass(generateAdapter = true)
internal data class EmergencyMessageResponse(
    val response: Response
) {
    fun toDomainModel(): List<EmergencyMessageInfo> {
        return this.response.body.items.item
            .filter { item -> item.symBlkMsgTyp?.contains("응급") ?: false }
            .map { item ->
                EmergencyMessageInfo(
                    emergencyMessage = item.symBlkMsg,
                    emergencyDate = formatDate(item.symBlkSttDtm.toString())
                )
            }
    }

    private fun formatDate(input: String): String {
        if (input.length != 14) return "-"

        return try {
            val year = input.substring(0, 4)
            val month = input.substring(4, 6)
            val day = input.substring(6, 8)
            val hour = input.substring(8, 10)
            val minute = input.substring(10, 12)
            val second = input.substring(12, 14)

            "$year.$month.$day $hour:$minute:$second"
        } catch (e: Exception) {
            "-"
        }
    }
}
