package kr.tekit.lion.presentation.ext

fun String.formatPhoneNumber(): String {
    if (length == 11) {
        return substring(0, 3) + "-" + substring(3, 7) + "-" + substring(7)
    } else if (length == 10) {
        return substring(0, 3) + "-" + substring(3, 6) + "-" + substring(6)
    }
    return this
}

fun String.formatBirthday(): String {
    if (length == 8) {
        return substring(0, 4) + "년 " + substring(4, 6) + "월 " + substring(6) + "일"
    }
    return this
}