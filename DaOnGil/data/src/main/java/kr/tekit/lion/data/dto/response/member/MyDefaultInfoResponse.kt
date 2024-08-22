package kr.tekit.lion.data.dto.response.member

import com.squareup.moshi.JsonClass
import kr.tekit.lion.daongil.data.dto.remote.response.member.MyDefaultInfoData
import kr.tekit.lion.domain.model.MyDefaultInfo

@JsonClass(generateAdapter = true)
internal data class MyDefaultInfoResponse(
    val code: Int,
    val data: MyDefaultInfoData,
    val message: String
){
    fun toDomainModel(): MyDefaultInfo {
        return MyDefaultInfo(
            date = data.date,
            name = data.name,
            profileImg = data.profileImg,
            reviewNum = data.reviewNum
        )
    }
}