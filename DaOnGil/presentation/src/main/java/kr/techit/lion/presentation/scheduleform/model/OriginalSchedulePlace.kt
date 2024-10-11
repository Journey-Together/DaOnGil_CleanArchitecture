package kr.techit.lion.presentation.scheduleform.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OriginalSchedulePlace(
    val placeId: Long,
    val name: String,
    val category: String,
    val imageUrl: String,
    // 무장애 유형 정보
    // 1 (지체 장애), 2 (시각 장애), 3 (청각 장애), 4 (유아 동반), 5 (고령자)
    val disability: List<Int>,
): Parcelable
