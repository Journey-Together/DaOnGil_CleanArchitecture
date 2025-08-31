package kr.techit.lion.presentation.concerntype

import kr.techit.lion.domain.model.concern.ConcernType
import kr.techit.lion.domain.model.concern.Concerns

data class ConcernTypUiState(
    val nickName: String = "",
    val concernType: Concerns = Concerns(),
) {
    fun changeSelectedType(type: ConcernType) = concernType.update(type)
}
