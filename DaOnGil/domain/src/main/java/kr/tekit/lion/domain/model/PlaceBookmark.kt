package kr.tekit.lion.domain.model

data class PlaceBookmark(
    val address: String,
    val disability: List<String>,
    val image: String,
    val name: String,
    val placeId: Long
)