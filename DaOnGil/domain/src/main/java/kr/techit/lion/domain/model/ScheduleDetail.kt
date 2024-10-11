package kr.techit.lion.domain.model

data class ScheduleDetail(
    val title : String,
    val startDate : String,
    val endDate : String,
    // 여행 시작일까지 남은 날짜 (이미 지난 경우 null?)
    val remainDate : String?,
    // 공개 일정 여부
    val isPublic: Boolean,
    // 본인 일정 여부
    val isWriter : Boolean,
    val nickname : String,
    val images : List<String>?,
    val dailyPlans: List<DailyPlan>,
    val writerId: Long,
    val reviewId: Long?,
    val content: String?,
    val grade: Double?,
    val reviewImages: List<String>?,
    val hasReview: Boolean,
    val profileUrl: String,
    val isBookmark: Boolean,
    val isReport: Boolean?
)
