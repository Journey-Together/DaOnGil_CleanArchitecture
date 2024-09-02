package kr.tekit.lion.presentation.ext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Date.formatDateValue(pattern: String) : String {
    val dateFormat = SimpleDateFormat(pattern, Locale.KOREA)
    return dateFormat.format(this)
}

/**
 * 시작일과 종료일(endDate)을 기반으로 전체 여행 일수를 반환
 *
 * @param endDate 여행 종료일
 * @return 여행의 총 일수
 */
fun Date.calculateDaysUntilEndDate(endDate: Date) : Int {
    // 날짜 차이
    val diffInMillies = kotlin.math.abs(endDate.time - this.time)
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillies).toInt()

    return diffInDays
}

/**
 * 현재 날짜로부터 지정한 일수 만큼 뒤의 날짜를  반환하는 확장 함수입니다.
 *
 * @param days 추가할 일수
 * @return 지정한 일수만큼 뒤의 날짜
 */
fun Date.addDays(days: Int): String {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_MONTH, days)

    val addedDateStr = SimpleDateFormat("M월 d일 (E)", Locale.KOREAN).format(calendar.time)

    return addedDateStr
}