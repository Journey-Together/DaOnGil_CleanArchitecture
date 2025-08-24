package kr.techit.lion.presentation.onboarding.model

data class OnBoardingUiState(
    val focusOn: FocusOn = FocusOn.ViewPager,
    val currentPage: Int = 0
)
