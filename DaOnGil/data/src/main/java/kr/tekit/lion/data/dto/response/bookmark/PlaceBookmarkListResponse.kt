package kr.tekit.lion.daongil.data.dto.remote.response.bookmark

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.BookmarkedPlace

@JsonClass(generateAdapter = true)
data class PlaceBookmarkListResponse(
    val code: Int,
    val data : List<PlaceBookmarkListData>,
    val message: String
) {
    fun toDomainModel(): List<BookmarkedPlace> {
        return data.map { PlaceBookmarkListData ->
            BookmarkedPlace(
                bookmarkedPlaceId = PlaceBookmarkListData.placeId,
                bookmarkedPlaceName = PlaceBookmarkListData.placeName
            )
        }
    }
}