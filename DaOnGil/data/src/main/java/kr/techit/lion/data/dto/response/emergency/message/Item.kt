package kr.techit.lion.data.dto.response.emergency.message

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Item(
    val dutyAddr: String?,
    val dutyName: String?,
    val emcOrgCod: String?,
    val hpid: String?,
    val rnum: Int?,
    val symBlkEndDtm: Long?,
    val symBlkMsg: String?,
    val symBlkMsgTyp: String?,
    val symBlkSttDtm: Long?,
    val symOutDspMth: String?,
    val symOutDspYon: String?,
    val symTypCod: String?,
    val symTypCodMag: String?
)
