package kr.tekit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.ReverseGecodes

@JsonClass(generateAdapter = true)
data class ReverseGecodeResponse(
    val results: List<Result?>?,
    val status: Status?
){
    fun toDomainModel(): ReverseGecodes {
        val domainResults = results?.mapNotNull { it?.toDomainModel() } ?: listOf()
        return ReverseGecodes(
            code = status?.code,
            results = domainResults
        )
    }
}
