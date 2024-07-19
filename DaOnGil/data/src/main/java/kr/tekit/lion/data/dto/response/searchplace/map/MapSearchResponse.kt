package kr.tekit.lion.data.dto.response.searchplace.map

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.MapSearchResult
import kr.tekit.lion.domain.model.MapSearchResultList

@JsonClass(generateAdapter = true)
data class MapSearchResponse(
    val code: Int,
    val data: List<PlaceRes>,  // Changed from `Data` to `List<PlaceRes>`
    val message: String
) {
    fun toDomainModel(): MapSearchResultList {
        return MapSearchResultList(
            places = data.map {
                MapSearchResult(
                    address = it.address,
                    disability = it.disability,
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