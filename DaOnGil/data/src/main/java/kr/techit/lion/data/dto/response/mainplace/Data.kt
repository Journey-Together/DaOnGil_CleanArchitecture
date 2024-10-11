package kr.techit.lion.data.dto.response.mainplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Data(
    val aroundPlaceList: List<AroundPlaceRes>,
    val recommendPlaceList: List<RecommendPlaceRes>
)