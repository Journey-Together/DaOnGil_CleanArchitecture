package kr.tekit.lion.domain.model

data class MapSearchResult(
    val address: String,
    val disability: List<Int>,
    val image: String,
    val mapX: String,
    val mapY: String,
    val name: String,
    val placeId: Int
)