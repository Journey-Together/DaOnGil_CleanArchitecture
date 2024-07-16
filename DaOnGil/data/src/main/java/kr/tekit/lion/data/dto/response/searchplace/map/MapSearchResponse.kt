package kr.tekit.lion.data.dto.response.searchplace.map

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.Place

@JsonClass(generateAdapter = true)
data class MapSearchResponse(
    val code: Int,
    val data: Data,
    val message: String
){
    fun toDomainModel(): List<Place>{
        return data.placeResList.map {
            Place(
                address = it.address,
                disability = it.disability,
                image = it.image,
                name = it.name,
                placeId = it.placeId
            )
        }
    }
}