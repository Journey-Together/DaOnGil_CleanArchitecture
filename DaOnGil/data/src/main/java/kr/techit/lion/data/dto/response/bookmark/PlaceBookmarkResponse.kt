package kr.techit.lion.daongil.data.dto.remote.response.bookmark

import com.squareup.moshi.JsonClass
import kr.techit.lion.domain.model.PlaceBookmark

@JsonClass(generateAdapter = true)
data class PlaceBookmarkResponse(
    val code: Int,
    val data: List<PlaceBookmarkData>,
    val message: String
) {
    fun toDomainModel(): List<PlaceBookmark> {
        return data.map { placeBookmarkData ->
            PlaceBookmark(
                address = placeBookmarkData.address,
                disability = placeBookmarkData.disability,
                image = placeBookmarkData.image,
                name = placeBookmarkData.name,
                placeId = placeBookmarkData.placeId
            )
        }
    }
}