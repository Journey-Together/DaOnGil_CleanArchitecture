package kr.tekit.lion.domain.model

data class SigunguCode (
    val areaCode: String,
    val sigunguCode: String,
    val sigunguName: String
)

data class SigunguList(
    val sigunguList: List<SigunguCode>
){
    fun findSigunguCode(sigunguName: String): String? {
        return sigunguList.find { it.sigunguName == sigunguName }?.sigunguCode
    }
}