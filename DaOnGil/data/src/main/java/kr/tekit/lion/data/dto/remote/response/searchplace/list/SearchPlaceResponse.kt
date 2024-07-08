package kr.tekit.lion.data.dto.remote.response.searchplace.list

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.AroundPlace
import kr.tekit.lion.domain.model.ListSearchResult

@JsonClass(generateAdapter = true)
data class SearchPlaceResponse(
    val code: Int,
    val data: Data,
    val message: String
)

fun SearchPlaceResponse.toDomainModel(): List<ListSearchResult> {
    return data.placeResList.map {
        ListSearchResult(
            AroundPlace(
                address = it.address,
                disability = it.disability,
                image = it.image,
                name = it.name,
                placeId = it.placeId
            ),
        )
    }
}


