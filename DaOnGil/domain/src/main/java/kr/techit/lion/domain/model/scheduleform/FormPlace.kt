package kr.techit.lion.domain.model.scheduleform

data class FormPlace(
    val placeId : Long,
    val placeImage : String?, // 사진 url
    val placeName : String, // 장소명
    val placeCategory : String, // 분류
)
