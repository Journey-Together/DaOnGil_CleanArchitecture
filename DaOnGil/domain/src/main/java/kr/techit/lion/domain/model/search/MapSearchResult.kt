package kr.techit.lion.domain.model.search

data class MapSearchResult(
    val address: String,
    val disability: List<String>,
    val image: String,
    val mapX: Double,
    val mapY: Double,
    val name: String,
    val placeId: Int
)

data class MapSearchResultList(
    val places: List<MapSearchResult>
){
    fun findPlaceDetail(placeId: Int): MapSearchResult? {
        return places.find { it.placeId == placeId }
    }
}
