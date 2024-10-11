package kr.techit.lion.presentation.ext

import kr.techit.lion.presentation.scheduleform.FormDateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 날짜를 pattern에 맞는 형식으로 반환
 *
 * @param pattern 날짜 형식 (FormDateFormat에 선언된 패턴 사용 가능)
 * @return 여행의 총 일수
 */
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
 * 현재 날짜로부터 지정한 일수 만큼 뒤의 날짜를 pattern에 맞는 형식으로 반환
 *
 * @param days 추가할 일수
 * @param pattern 날짜 형식 ("M월 d일 (E)", "yyyy-MM-dd" 등 FormDateFormat에 선언된 패턴 사용 가능)
 * @return 지정한 일수만큼 뒤의 날짜
 */
fun Date.addDays(days: Int, pattern: String): String {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_MONTH, days)

    val addedDateStr = SimpleDateFormat(pattern, Locale.KOREAN).format(calendar.time)

    return addedDateStr
}


/**
 * String 형태("yyyy-MM-dd")의 날짜를 Date 객체로 변환
 *
 * @return 변환된 Date 객체
 */
fun String.convertStringToDate() : Date {
    val formatter = SimpleDateFormat(FormDateFormat.YYYY_MM_DD, Locale.KOREA)
    val date = formatter.parse(this) ?: Date()

    return date
}

/**
 * Date 객체 두 개를
 * "일정 시작일 yyyy년 M월 d일, 종료일 yyyy년 M월 d일"
 * 형태로 변환
 *
 * @return "일정 시작일 yyyy년 M월 d일, 종료일 yyyy년 M월 d일"
 */
fun Date.convertPeriodToDate(endDate: Date): String {
    val startDateString = this.formatDateValue(FormDateFormat.YYYY_M_D)
    val endDateString = endDate.formatDateValue(FormDateFormat.YYYY_M_D)

    return "일정 시작일 $startDateString, 종료일 $endDateString"
}