package kr.tekit.lion.data.dto.response.member

import com.squareup.moshi.JsonClass
import kr.tekit.lion.daongil.data.dto.remote.response.member.MyInfoData
import kr.tekit.lion.domain.model.MyInfo

@JsonClass(generateAdapter = true)
internal data class MyInfoResponse (
    val code: Int,
    val message: String,
    val data: MyInfoData
){
    fun toDomainModel(): MyInfo {
        return MyInfo(
            name = data.name,
            nickname = data.nickname,
            phone = data.phone,
            profileImage = data.profileImage,
            bloodType = data.bloodType,
            birth = data.birth,
            disease = data.disease,
            allergy = data.allergy,
            medication = data.medication,
            part1Rel = data.part1Rel,
            part1Phone = data.part1Phone,
            part2Rel = data.part2Rel,
            part2Phone = data.part2Phone
        )
    }
}