package kr.techit.lion.presentation.main.model

import kr.techit.lion.domain.model.search.ListSearchResultList
import java.util.UUID

sealed class ListSearchUIModel(val uuid: UUID = UUID.randomUUID())

data class CategoryModel(
    val optionState: MutableMap<DisabilityType, Int>
) : ListSearchUIModel()

data class AreaModel(
    val areas: List<String>,
) : ListSearchUIModel()

data class SigunguModel(
    val sigungus: List<String>,
    val selectedSigungu: String
) : ListSearchUIModel()

data class SortModel(
    val totalItemCount: Int,
): ListSearchUIModel()

data class NoPlaceModel(
    val msg: String = "검색 결과가 없어요\n다시 검색 해주세요"
) : ListSearchUIModel()

data class PlaceModel(
    val placeName: String,
    val placeAddr: String,
    val placeId: Long,
    val placeImg: String = "",
    val disability: List<String> = emptyList(),
    val itemCount: Int
) : ListSearchUIModel()

fun ListSearchResultList.toUiModel(): List<PlaceModel> =
    this.places.map {
        PlaceModel(
            placeName = it.place.name,
            placeAddr = it.place.address,
            placeId = it.place.placeId,
            placeImg = it.place.image,
            disability = it.place.disability,
            itemCount = itemSize
        )
    }


