package kr.techit.lion.presentation.ext

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

fun String.isBirthdayValid(): Boolean {
    val birthdayPattern = "^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$"
    return this.matches(birthdayPattern.toRegex())
}

fun String.isPhoneNumberValid(): Boolean {
    val phonePattern = "^010\\d{4}\\d{4}$"
    return this.matches(phonePattern.toRegex())
}

fun String. pronounceEachCharacter(): String {
    return this.replace("", " ").trim()
}