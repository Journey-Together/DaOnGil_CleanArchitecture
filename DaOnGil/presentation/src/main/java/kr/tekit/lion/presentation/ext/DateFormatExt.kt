package kr.tekit.lion.presentation.ext

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatDateValue(pattern: String) : String {
    val dateFormat = SimpleDateFormat(pattern, Locale.KOREA)
    return dateFormat.format(this)
}