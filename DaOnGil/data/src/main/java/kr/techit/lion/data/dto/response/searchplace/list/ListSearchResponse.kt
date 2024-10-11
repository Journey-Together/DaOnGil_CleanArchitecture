package kr.techit.lion.data.dto.response.searchplace.list

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.Place
import kr.techit.lion.domain.model.search.ListSearchResult

@JsonClass(generateAdapter = true)
internal data class SearchPlaceResponse(
    val code: Int,
    val data: Data,
    val message: String
)

internal fun SearchPlaceResponse.toDomainModel(): List<ListSearchResult> {
    return data.placeResList.map {
        ListSearchResult(
            Place(
                address = it.address,
                disability = it.disability,
                image = it.image,
                name = it.name,
                placeId = it.placeId
            ),
        )
    }
}


