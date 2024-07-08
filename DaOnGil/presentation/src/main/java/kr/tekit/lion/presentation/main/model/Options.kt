package kr.tekit.lion.presentation.main.model

sealed class Options(val code: Long) {
    data object Parking: Options(1)
    data object Wheelchair: Options(6)
    data object Elevator: Options(7)
    data object RestRoom: Options(8)
    data object Seat: Options(9)
    data object Braileblock: Options(13)
    data object HelpDog: Options(14)
    data object Guide: Options(15)
    data object AudioGuide: Options(16)
    data object SignGuide: Options(21)
    data object VideoGuide: Options(22)
    data object Stroller: Options(25)
    data object LactationRoom: Options(26)
    data object BabySpareChair: Options(27)
    data object WheelchairLent: Options(29)
}