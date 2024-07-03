package kr.tekit.lion.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityOnBoardingBinding
import kr.tekit.lion.presentation.login.LoginActivity
import kr.tekit.lion.presentation.main.MainActivity
import kr.tekit.lion.presentation.splash.adapter.OnBoardingImageVPAdapter
import kr.tekit.lion.presentation.login.model.OnBoardingPage

class OnBoardingActivity : AppCompatActivity() {

    private val binding: ActivityOnBoardingBinding by lazy {
        ActivityOnBoardingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pages = listOfNotNull(
            ContextCompat.getDrawable(this@OnBoardingActivity, R.drawable.onboarding_first)
                ?.let {
                    OnBoardingPage(it, "여행의 모든 문턱을 낮추다", "다온길에서 무장애 여행 정보를 확인하세요", "")
                },
            ContextCompat.getDrawable(this@OnBoardingActivity, R.drawable.onboarding_second)
                ?.let {
                    OnBoardingPage(it, "안전한 여행의 첫 걸음", "언제 어디서든 응급 지원정보를 확인하세요", "")
                },
            ContextCompat.getDrawable(this@OnBoardingActivity, R.drawable.onboarding_third)
                ?.let {
                    OnBoardingPage(it, "한곳에서 완성되는 여행", "여행 계획부터 후기 공유까지 한번에 관리해요", "")
                },
            ContextCompat.getDrawable(this@OnBoardingActivity, R.drawable.onboarding_last)
                ?.let {
                    OnBoardingPage(
                        it,
                        "행복이 다가온 여행길, 다온길 ",
                        "모두를 위한 행운 이정표",
                        "누구든 떠날 자유, 모두가 누릴 행복"
                    )
                }
        )

        val onBoardingVPAdapter = OnBoardingImageVPAdapter(pages)

        binding.onBoardingVp.adapter = onBoardingVPAdapter
        binding.onBoardingVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.onBoardingVpIndicator.setViewPager(binding.onBoardingVp)

        val nextButton = binding.onBoardingFirstNextButton
        val textView = binding.onBoardingFirstTextView3

        binding.onBoardingVp.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 마지막 페이지인지 확인
                if (position == binding.onBoardingVp.adapter?.itemCount?.minus(1)) {
                    nextButton.text = "시작하기"
                    textView.text = "로그인/회원가입 진행하기"

                    nextButton.setOnClickListener {
                        val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    textView.setOnClickListener {
                        val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
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
                        binding.onBoardingVp.currentItem =
                            binding.onBoardingVp.adapter?.itemCount?.minus(1) ?: 0
                    }
                }
            }
        })
    }
}