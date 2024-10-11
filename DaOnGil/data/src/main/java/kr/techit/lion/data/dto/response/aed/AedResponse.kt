package kr.techit.lion.data.dto.response.aed

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.AedMapInfo

@JsonClass(generateAdapter = true)
internal data class AedResponse(
    val response: Response
) {
    fun toDomainModel(): List<AedMapInfo>{
        return this.response.body.items.item.map { item ->
            AedMapInfo(
                aedName = item.org,
                aedAdress = item.buildAddress,
                managerTel = item.managerTel,
                aedTel = item.clerkTel,
                aedLocation = item.buildPlace,
                monTime = convertTimeFormat(item.monSttTme.toString() + "-" + item.monEndTme.toString()),
                tueTime = convertTimeFormat(item.tueSttTme.toString() + "-" + item.tueEndTme.toString()),
                wedTime = convertTimeFormat(item.wedSttTme.toString() + "-" + item.wedEndTme.toString()),
                thuTime = convertTimeFormat(item.thuSttTme.toString() + "-" + item.thuEndTme.toString()),
                friTime = convertTimeFormat(item.friSttTme.toString() + "-" + item.friEndTme.toString()),
                satTime = convertTimeFormat(item.satSttTme.toString() + "-" + item.satEndTme.toString()),
                sunTime = convertTimeFormat(item.sunSttTme.toString() + "-" + item.sunEndTme.toString()),
                holTime = convertTimeFormat(item.holSttTme.toString() + "-" + item.holEndTme.toString()),
                sunAvailable = updateSunAvailable(
                    item.sunFrtYon.toString(),
                    item.sunScdYon.toString(), item.sunThiYon.toString(),
                    item.sunFurYon.toString(), item.sunFifYon.toString()
                ),
                aedLon = item.wgs84Lon,
                aedLat = item.wgs84Lat
            )
        }
    }

    fun convertTimeFormat(timeRange: String): String {
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


    fun updateSunAvailable(first: String, second: String, third: String, fourth: String, fifth: String, ) : String{
        var sunAvailable = ""

        if(first == "Y") {
            sunAvailable += "첫째주"
        }
        if(second == "Y") {
            if (sunAvailable.isNotEmpty()) sunAvailable += ", "
            sunAvailable += "둘째주"
        }
        if(third == "Y") {
            if (sunAvailable.isNotEmpty()) sunAvailable += ", "
            sunAvailable += "셋째주"
        }
        if (fourth == "Y") {
            if (sunAvailable.isNotEmpty()) sunAvailable += ", "
            sunAvailable += "넷째주"

        }
        if (fifth == "Y"){
            if (sunAvailable.isNotEmpty()) sunAvailable += ", "
            sunAvailable += "다섯째주"

        }

        return sunAvailable
    }
}