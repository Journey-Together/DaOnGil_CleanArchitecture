package kr.techit.lion.presentation.home.model

sealed class HomeViewPagerPage {
    data object SearchPlace: HomeViewPagerPage()
    data object Emergency: HomeViewPagerPage()
    data object Schedule: HomeViewPagerPage()

    companion object {
        fun fromPosition(position: Int): HomeViewPagerPage {
            return when (position) {
                0 -> SearchPlace
                1 -> Emergency
                2 -> Schedule
                else -> throw IllegalArgumentException("Invalid position : $position")
            }
        }
    }
}