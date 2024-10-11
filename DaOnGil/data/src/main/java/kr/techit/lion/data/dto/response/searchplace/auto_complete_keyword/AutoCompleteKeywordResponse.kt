package kr.techit.lion.data.dto.response.searchplace.auto_complete_keyword

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.search.AutoCompleteKeyword

@JsonClass(generateAdapter = true)
data class AutoCompleteKeywordResponse(
    val code: Int,
    val data: List<Data>,
    val message: String
) {
    fun toDomainModel(): List<AutoCompleteKeyword> {
        return data.map {
            AutoCompleteKeyword(
                keyword = it.keyword,
                placeId = it.placeId
            )
        }
    }
}

