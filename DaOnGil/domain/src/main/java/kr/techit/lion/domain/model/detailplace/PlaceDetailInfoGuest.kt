package kr.techit.lion.domain.model.detailplace

data class PlaceDetailInfoGuest (
    val code: Int,
    val address: String,
    val bookmarkNum: Int,
    val category: String,
    val disability: List<Int>,
    val image: String?,
    val latitude: String,
    val longitude: String,
    val name: String,
    val overview: String,
    val tel: String,
    val homepage: String,
    val placeId: Long,
    val reviewList: List<Review>?,
    val subDisability: List<SubDisability>?
)