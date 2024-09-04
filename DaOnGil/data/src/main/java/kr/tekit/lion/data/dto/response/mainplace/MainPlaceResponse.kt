package kr.tekit.lion.data.dto.response.mainplace

import com.squareup.moshi.JsonClass
import kr.tekit.lion.domain.model.mainplace.AroundPlace
import kr.tekit.lion.domain.model.mainplace.PlaceMainInfo
import kr.tekit.lion.domain.model.mainplace.RecommendPlace

@JsonClass(generateAdapter = true)
internal data class MainPlaceResponse(
    val code: Int,
    val data: Data,
    val message: String
) {
    fun toDomainModel(): PlaceMainInfo {
        return PlaceMainInfo(
            aroundPlaceList = data.aroundPlaceList.map {
                AroundPlace(
                    address = it.address,
                    disability = it.disability,
                    image = it.image,
                    name = it.name,
                    placeId = it.placeId)
            },
            recommendPlaceList = data.recommendPlaceList.map {
                RecommendPlace(
                    address = it.address,
                    disability = it.disability,
                    image = it.image,
                    name = it.name,
                    placeId = it.placeId
                )
            }
        )
    }
}