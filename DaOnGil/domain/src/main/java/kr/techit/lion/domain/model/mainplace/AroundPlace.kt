package kr.techit.lion.domain.model.mainplace

data class AroundPlace (
    val address: String,
    val disability: List<String>,
    val image: String,
    val name: String,
    val placeId: Long
)