package kr.techit.lion.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityOnBoardingBinding
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.login.LoginActivity
import kr.techit.lion.presentation.onboarding.model.FocusOn
import kr.techit.lion.presentation.onboarding.vm.OnBoardingViewModel
import kr.techit.lion.presentation.main.MainActivity
import kr.techit.lion.presentation.splash.adapter.OnBoardingImageVPAdapter

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOnBoardingBinding.inflate(layoutInflater)
    }
    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUiState()
        initAccessibility()
        setUpOnBoardingViewPager()
    }

    private fun initAccessibility() {
        if (this.isTallBackEnabled()) {
            this.announceForAccessibility(getString(R.string.text_script_guide_onboarding))
        }
    }

    private fun setupUiState() {
        repeatOnStarted {
            viewModel.uiState.collect { state ->
                if (isTallBackEnabled()) {
                    setViewPagerTallBack(state.currentPage, state.focusOn)
                }
            }
        }
    }

    private fun setUpOnBoardingViewPager() {
        val onboardingPageProvider = OnBoardingPageProvider(this)
        val onboardingPages = onboardingPageProvider.onBoardingPages()
        val onBoardingVPAdapter = OnBoardingImageVPAdapter(onboardingPages)

        with(binding) {
            if (this@OnBoardingActivity.isTallBackEnabled()) {
                setUpOnBoardingViewPagerAccessibility()
            }

            setUpOnBoardingViewPagerCallback()

            onBoardingVp.adapter = onBoardingVPAdapter
            onBoardingVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            onBoardingVpIndicator.setViewPager(binding.onBoardingVp)
        }
    }

    private fun setUpOnBoardingViewPagerAccessibility() {
        with(binding) {
            onBoardingVp.accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun sendAccessibilityEvent(host: View, eventType: Int) {
                    super.sendAccessibilityEvent(host, eventType)
                    if (eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                        viewModel.setFocusOn(FocusOn.ViewPager)
                    }
                }
            }

            btnNext.accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun sendAccessibilityEvent(host: View, eventType: Int) {
                    super.sendAccessibilityEvent(host, eventType)
                    if (eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                        viewModel.setFocusOn(FocusOn.NextButton)
                    }
                }
            }

            btnSkip.accessibilityDelegate = object : View.AccessibilityDelegate() {
                override fun sendAccessibilityEvent(host: View, eventType: Int) {
                    super.sendAccessibilityEvent(host, eventType)
                    if (eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
                        viewModel.setFocusOn(FocusOn.SkipButton)
                    }
                }
            }
        }
    }

    private fun setUpOnBoardingViewPagerCallback() = with(binding) {
        onBoardingVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val lastPosition =
                    binding.onBoardingVp.adapter?.itemCount ?: ONBOARDING_INITIAL_POSITION
                val isLastPage = position == lastPosition - ONBOARDING_MOVE_STEP_COUNT

                if (isLastPage) setupLastPage()
                else setupNormalPage(position, lastPosition)

                viewModel.setCurrentPage(position)
            }
        })
    }

    private fun setupNormalPage(position: Int, lastPosition: Int) = with(binding) {
        btnNext.text = getString(R.string.text_onboarding_next)
        btnNext.setOnClickListener {
            onBoardingVp.currentItem = position + ONBOARDING_MOVE_STEP_COUNT
        }

        btnSkip.text = getString(R.string.text_onboarding_skip)
        btnSkip.setOnClickListener {
            onBoardingVp.currentItem = lastPosition - ONBOARDING_MOVE_STEP_COUNT
        }
    }

    private fun setupLastPage() = with(binding) {
        btnNext.text = getString(R.string.text_app_start)
        btnNext.setOnClickListener { moveToMain() }

        btnSkip.text = getString(R.string.text_login_signup)
        btnSkip.setOnClickListener { moveToLogin() }

        if (this@OnBoardingActivity.isTallBackEnabled()) {
            this@OnBoardingActivity.announceForAccessibility(
                getString(R.string.text_script_guide_last_onboarding_page)
            )
        }
    }

    private fun setViewPagerTallBack(position: Int, focusOn: FocusOn) {
        when (position) {
            1 -> {
                if (focusOn != FocusOn.ViewPager) {
                    this@OnBoardingActivity.announceForAccessibility(
                        getString(R.string.text_onboarding_second_text1) +
                                getString(R.string.text_onboarding_second_text2)
                    )
                }
            }
            2 -> if (focusOn != FocusOn.ViewPager) {
                this@OnBoardingActivity.announceForAccessibility(
                    getString(R.string.text_onboarding_third_text1) +
                            getString(R.string.text_onboarding_third_text2)
                )
            }
            3 -> if (focusOn != FocusOn.ViewPager) {
                this@OnBoardingActivity.announceForAccessibility(
                    getString(R.string.text_onboarding_fourth_text1) +
                            getString(R.string.text_onboarding_fourth_text2) +
                            getString(R.string.text_onboarding_fourth_text3)
                )
            }
        }
    }

    private fun moveToMain() {
        startActivity(MainActivity.newIntent(this))
        finish()
    }

    private fun moveToLogin() {
        val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val ONBOARDING_INITIAL_POSITION = 0
        private const val ONBOARDING_MOVE_STEP_COUNT = 1
    }
}
