package kr.tekit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.ReverseGeocodes

@JsonClass(generateAdapter = true)
data class ReverseGeocodeResponse(
    val results: List<Result?>?,
    val status: Status?
){
    fun toDomainModel(): ReverseGeocodes {
        val domainResults = results?.mapNotNull { it?.toDomainModel() } ?: listOf()
        return ReverseGeocodes(
            code = status?.code,
            results = domainResults
        )
    }
}
