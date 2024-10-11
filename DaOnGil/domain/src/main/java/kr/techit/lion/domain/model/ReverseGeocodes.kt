package kr.techit.lion.domain.model

data class ReverseGeocodes(
    val code: Int?,
    val results: List<ReverseGeocode>,
)

data class ReverseGeocode(
    val area: String?,
    val areaDetail: String?
)