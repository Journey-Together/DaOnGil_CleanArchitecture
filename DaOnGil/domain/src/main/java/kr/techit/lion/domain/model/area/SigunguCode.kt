package kr.techit.lion.domain.model.area

data class SigunguCode (
    val areaCode: String,
    val sigunguCode: String,
    val sigunguName: String
)

data class SigunguCodeList(
    val sigunguList: List<SigunguCode>
){
    fun getSigunguName(): List<String>{
        return sigunguList.map { it.sigunguName }
    }

    fun findSigunguCode(sigunguName: String): String {
        return sigunguList.find { it.sigunguName == sigunguName }?.sigunguCode ?: ""
    }
}