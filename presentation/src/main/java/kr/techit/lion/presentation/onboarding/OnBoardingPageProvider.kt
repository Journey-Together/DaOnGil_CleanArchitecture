package kr.techit.lion.presentation.onboarding

import kr.techit.lion.presentation.R
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class OnBoardingPageProvider(
    private val context: Context,
) {
    fun onBoardingPages(): List<OnBoardingPage> {
        return listOf(
            OnBoardingPage(
                getSafeDrawable(R.drawable.onboarding_first),
                context.getString(R.string.text_onboarding_first_text1),
                context.getString(R.string.text_onboarding_first_text2),
            ),
            OnBoardingPage(
                getSafeDrawable(R.drawable.onboarding_second),
                context.getString(R.string.text_onboarding_second_text1),
                context.getString(R.string.text_onboarding_second_text2),
            ),
            OnBoardingPage(
                getSafeDrawable(R.drawable.onboarding_third),
                context.getString(R.string.text_onboarding_third_text1),
                context.getString(R.string.text_onboarding_third_text2),
            ),
            OnBoardingPage(
                getSafeDrawable(R.drawable.onboarding_last),
                context.getString(R.string.text_onboarding_fourth_text1),
                context.getString(R.string.text_onboarding_fourth_text2),
                context.getString(R.string.text_onboarding_fourth_text3)
            )
        )
    }

    private fun getSafeDrawable(resourceId: Int): Drawable =
        requireNotNull(ContextCompat.getDrawable(context, resourceId)) {
            DRAWABLE_RESOURCE_NOT_FOUNT
        }

    companion object {
        private const val DRAWABLE_RESOURCE_NOT_FOUNT = "아이디에 해당하는 리소스를 찾을 수 없습니다."
    }
}
