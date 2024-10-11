package kr.techit.lion.data.dto.response.member

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ConcernTypeData(
    val isPhysical: Boolean,
    val isHear: Boolean,
    val isVisual: Boolean,
    val isElderly: Boolean,
    val isChild: Boolean,
)