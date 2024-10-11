package kr.techit.lion.domain.model.placereviewlist

data class PlaceReviewInfo (
    val placeReviewList: List<PlaceReview>,
    val placeImg: String,
    val pageNo: Int?,
    val pageSize: Int?,
    val placeAddress: String,
    val placeName: String,
    val totalPages: Int?,
    val reviewNum: Int
)
