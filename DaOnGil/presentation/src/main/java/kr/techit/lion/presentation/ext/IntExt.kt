package kr.techit.lion.presentation.ext


/**
 * 현재 첨부된 사진 장수를 표기해주기 위한 확장함수
 * - 1부터 4까지의 숫자를 한, 두, 세, 네로 변환
 *
 * @return 한, 두, 세, 네
 */
fun Int.numberToKorean(): String {
    return when (this) {
        1 -> "한"
        2 -> "두"
        3 -> "세"
        4 -> "네"
        else -> this.toString()
    }
}