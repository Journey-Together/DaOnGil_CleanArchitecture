package kr.tekit.lion.presentation.main.model

import kr.tekit.lion.domain.model.ListSearchResultList
import kr.tekit.lion.presentation.R

sealed class ListSearchUIModel(val id: Int)

data object CategoryModel : ListSearchUIModel(R.layout.item_list_search_category)

data object AreaModel : ListSearchUIModel(R.layout.item_list_search_area)

data class NoPlaceModel(
    val msg: String = "검색 결과가 없어요\n다시 검색 해주세요"
) : ListSearchUIModel(R.layout.item_no_place)

data class PlaceModel(
    val placeName: String="",
    val placeAddr: String="",
    val placeId: String="",
    val placeImg: String="",
    val disability: List<String> = emptyList(),
    val itemCount: Int = 0
) : ListSearchUIModel(R.layout.item_place_high)

fun ListSearchResultList.toUiModel(): List<PlaceModel> =
    this.places.map {
        PlaceModel(
            placeName = it.place.name,
            placeAddr = it.place.address,
            placeId = it.place.placeId.toString(),
            placeImg = it.place.image,
            disability = it.place.disability,
            itemCount = itemSize
        )
    }


