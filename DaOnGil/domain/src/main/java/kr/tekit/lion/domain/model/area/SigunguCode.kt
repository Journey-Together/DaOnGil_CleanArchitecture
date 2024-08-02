package kr.tekit.lion.domain.model.area

data class SigunguCode (
    val areaCode: String,
    val sigunguCode: String,
    val sigunguName: String
)

data class SigunguCodeList(
    val sigunguList: List<SigunguCode>
){
    fun findSigunguCode(sigunguName: String): String? {
        return sigunguList.find { it.sigunguName == sigunguName }?.sigunguCode
    }
}