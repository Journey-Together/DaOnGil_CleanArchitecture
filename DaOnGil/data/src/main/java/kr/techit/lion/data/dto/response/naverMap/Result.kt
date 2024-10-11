package kr.techit.lion.data.dto.response.naverMap

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.ReverseGeocode

@JsonClass(generateAdapter = true)
internal data class Result(
    val code: Code?,
    val name: String?,
    val region: Region?
){
    fun toDomainModel(): ReverseGeocode {
        return ReverseGeocode(
            area = region?.area1?.name,
            areaDetail = region?.area2?.name
        )
    }
}
