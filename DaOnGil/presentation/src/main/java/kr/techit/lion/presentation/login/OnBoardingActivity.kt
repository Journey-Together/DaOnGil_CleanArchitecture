package kr.techit.lion.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityOnBoardingBinding
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.login.model.FocusOn
import kr.techit.lion.presentation.login.model.OnBoardingPage
import kr.techit.lion.presentation.login.vm.OnBoardingViewModel
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

        if (this.isTallBackEnabled()) {
            this.announceForAccessibility(getString(R.string.text_script_guide_onboarding))
        }

        val pages = listOfNotNull(
            ContextCompat.getDrawable(this, R.drawable.onboarding_first)?.let {
                OnBoardingPage(
                    it,
                    getString(R.string.text_onboarding_first_text1),
                    getString(R.string.text_onboarding_first_text2),
                    ""
                )
            },
            ContextCompat.getDrawable(this, R.drawable.onboarding_second)?.let {
                OnBoardingPage(
                    it,
                    getString(R.string.text_onboarding_second_text1),
                    getString(R.string.text_onboarding_second_text2),
                    ""
                )
            },
            ContextCompat.getDrawable(this, R.drawable.onboarding_third)?.let {
                OnBoardingPage(
                    it,
                    getString(R.string.text_onboarding_third_text1),
                    getString(R.string.text_onboarding_third_text2),
                    ""
                )
            },
            ContextCompat.getDrawable(this, R.drawable.onboarding_last)?.let {
                OnBoardingPage(
                    it, getString(R.string.text_onboarding_fourth_text1),
                    getString(R.string.text_onboarding_fourth_text2),
                    getString(R.string.text_onboarding_fourth_text3)
                )
            }
        )

        val onBoardingVPAdapter = OnBoardingImageVPAdapter(pages)

        with(binding) {
            if (this@OnBoardingActivity.isTallBackEnabled()) {
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
            onBoardingVp.adapter = onBoardingVPAdapter
            onBoardingVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            onBoardingVpIndicator.setViewPager(binding.onBoardingVp)

            val nextButton = btnNext
            val textView = btnSkip

            onBoardingVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 마지막 페이지인지 확인
                    if (position == onBoardingVp.adapter?.itemCount?.minus(1)) {
                        nextButton.text = "시작하기"
                        textView.text = "로그인/회원가입 진행하기"

                        nextButton.setOnClickListener {
                            val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        textView.setOnClickListener {
                            startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
                        }

                        if (this@OnBoardingActivity.isTallBackEnabled()) {
                            repeatOnStarted {
                                delay(3000)
                                this@OnBoardingActivity.announceForAccessibility(
                                    getString(R.string.text_script_guide_last_onboarding_page)
                                )
                            }
                        }

                    } else {
                        nextButton.text = "다음"
                        textView.text = "건너뛰기"

                        nextButton.setOnClickListener {
                            // 다음 페이지로 이동
                            binding.onBoardingVp.currentItem = position + 1
                        }

                        textView.setOnClickListener {
                            // 마지막 페이지로 이동
                            binding.onBoardingVp.currentItem = binding.onBoardingVp.adapter?.itemCount?.minus(1) ?: 0
                        }
                    }

                    if (this@OnBoardingActivity.isTallBackEnabled()) {
                        when (position) {
                            1 -> {
                                if (viewModel.focusOn.value != FocusOn.ViewPager) {
                                    this@OnBoardingActivity.announceForAccessibility(
                                        getString(R.string.text_onboarding_second_text1) +
                                                getString(R.string.text_onboarding_second_text2)
                                    )
                                }
                            }
                            2 -> if (viewModel.focusOn.value != FocusOn.ViewPager) {
                                this@OnBoardingActivity.announceForAccessibility(
                                    getString(R.string.text_onboarding_third_text1) +
                                            getString(R.string.text_onboarding_third_text2)
                                )
                            }
                            3 -> if (viewModel.focusOn.value != FocusOn.ViewPager) {
                                this@OnBoardingActivity.announceForAccessibility(
                                    getString(R.string.text_onboarding_fourth_text1) +
                                            getString(R.string.text_onboarding_fourth_text2) +
                                            getString(R.string.text_onboarding_fourth_text3)
                                )
                            }
                        }
                    }
                }
            })
        }
    }
}