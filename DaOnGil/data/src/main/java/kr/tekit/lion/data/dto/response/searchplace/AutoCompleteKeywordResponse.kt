package kr.tekit.lion.data.dto.response.searchplace

import kr.tekit.lion.domain.model.search.AutoCompleteKeyword

data class AutoCompleteKeywordResponse(
    val code: Int,
    val message: String,
    val data: List<String>
){
    fun toDomainModel(): AutoCompleteKeyword{
        return AutoCompleteKeyword(keywordList = data)
    }
}