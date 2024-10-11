package kr.techit.lion.data.dto.response.areacode

import kr.techit.lion.domain.model.area.AreaCode
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AreaCodeResponse(
    val response: Response
){
    fun toDomainModel(): List<AreaCode> {
        return response.body.items.item.map {
            AreaCode(it.code, it.name)
        }
    }
}




