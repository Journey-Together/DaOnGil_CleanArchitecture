package kr.techit.lion.domain.model.detailplace

data class PlaceDetailInfo (
    val code: Int,
    val address: String,
    var bookmarkNum: Int,
    val category: String,
    val disability: List<Int>,
    val image: String?,
    var isMark: Boolean,
    val latitude: String,
    val longitude: String,
    val name: String,
    val overview: String,
    val tel: String,
    val homepage: String,
    val isReview: Boolean,
    val placeId: Long,
    val reviewList: List<Review>?,
    val subDisability: List<SubDisability>?
)