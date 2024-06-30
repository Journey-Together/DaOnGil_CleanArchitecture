package kr.tekit.lion.data.dto.remote.response.areacode

import kr.tekit.lion.domain.model.AreaCode
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AreaCodeResponse(
    val response: Response
){
    fun toDomainModel(): List<AreaCode> {
        return response.body.items.item.map {
            AreaCode(it.code, it.name)
        }
    }
}




