package kr.tekit.lion.presentation.main.model

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
