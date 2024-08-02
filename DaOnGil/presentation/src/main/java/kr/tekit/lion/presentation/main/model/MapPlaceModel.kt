package kr.tekit.lion.presentation.main.model

import kr.tekit.lion.domain.model.MapSearchResultList

data class MapPlaceModel(
    val placeName: String,
    val placeAddr: String,
    val placeId: String,
    val placeImg: String="",
    val latitude: Double,
    val longitude: Double,
    val disability: List<String> = emptyList(),
)

fun MapSearchResultList.toUiModel(): List<MapPlaceModel> =
    this.places.map {
        MapPlaceModel(
            placeName = it.name,
            placeAddr = it.address,
            placeId = it.placeId.toString(),
            placeImg = it.image,
            latitude = it.mapY,
            longitude = it.mapX,
            disability = it.disability,
        )
    }