package kr.tekit.lion.domain.model.mainplace

data class PlaceMainInfo (
    val aroundPlaceList : List<AroundPlace>,
    val recommendPlaceList : List<RecommendPlace>
)