package kr.tekit.lion.presentation.main.model

import kr.tekit.lion.domain.model.ListSearchResultList

sealed class ListSearchUIModel

data object CategoryModel : ListSearchUIModel()

data object AreaModel : ListSearchUIModel()

data class PlaceModel(
    val placeName: String,
    val placeAddr: String,
    val placeId: String,
    val placeImg: String,
    val disability: List<String>
) : ListSearchUIModel()

fun ListSearchResultList.toUiModel(): List<PlaceModel> =
    this.places.map {
        PlaceModel(
            placeName = it.place.name,
            placeAddr = it.place.address,
            placeId = it.place.placeId.toString(),
            placeImg = it.place.image,
            disability = it.place.disability
        )
    }


