package kr.techit.lion.data.dto.response.searchplace.map

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.search.MapSearchResult
import kr.techit.lion.domain.model.search.MapSearchResultList

@JsonClass(generateAdapter = true)
internal data class MapSearchResponse(
    val code: Int,
    val data: List<PlaceRes>,
    val message: String
) {
    fun toDomainModel(): MapSearchResultList {
        return MapSearchResultList(
            places = data.map {
                MapSearchResult(
                    address = it.address,
                    disability = it.disability,  // assuming disabilities are integers
                    image = it.image,
                    name = it.name,
                    mapX = it.mapX,
                    mapY = it.mapY,
                    placeId = it.placeId
                )
            }
        )
    }
}
