package kr.techit.lion.data.dto.response.pharmacy

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.PharmacyMapInfo

@JsonClass(generateAdapter = true)
internal data class PharmacyResponse(
    val response: Response
) {
    fun toDomainModel(): List<PharmacyMapInfo> {
        return this.response.body.items.item.map {item ->
            PharmacyMapInfo(
                pharmacyAddress = item.dutyAddr,
                pharmacyName = item.dutyName,
                pharmacyTel = item.dutyTel1,
                pharmacyLocation = item.dutyMapimg ?: "정보 없음",
                monTime = convertTimeFormat(item.dutyTime1s.toString() + "-" + item.dutyTime1c.toString()),
                tueTime = convertTimeFormat(item.dutyTime2s.toString() + "-" + item.dutyTime2c.toString()),
                wedTime = convertTimeFormat(item.dutyTime3s.toString() + "-" + item.dutyTime3c.toString()),
                thuTime = convertTimeFormat(item.dutyTime4s.toString() + "-" + item.dutyTime4c.toString()),
                friTime = convertTimeFormat(item.dutyTime5s.toString() + "-" + item.dutyTime5c.toString()),
                satTime = convertTimeFormat(item.dutyTime6s.toString() + "-" + item.dutyTime6c.toString()),
                sunTime = convertTimeFormat(item.dutyTime7s.toString() + "-" + item.dutyTime7c.toString()),
                holTime = convertTimeFormat(item.dutyTime8s.toString() + "-" + item.dutyTime8c.toString()),
                pharmacyLat = item.wgs84Lat,
                pharmacyLon = item.wgs84Lon
            )

        }
    }

    private fun convertTimeFormat(timeRange: String): String {
        val times = timeRange.split("-")
        if (times.size != 2) {
            return "정보 없음"
        }

        if (times.any { it.length != 4 || !it.all { char -> char.isDigit() } }) {
            return "정보 없음"
        }

        val startTime = times[0].padStart(4, '0')
        val endTime = times[1].padStart(4, '0')

        val formattedStartTime = startTime.substring(0, 2) + ":" + startTime.substring(2, 4)
        val formattedEndTime = endTime.substring(0, 2) + ":" + endTime.substring(2, 4)

        return "$formattedStartTime-$formattedEndTime"
    }
}