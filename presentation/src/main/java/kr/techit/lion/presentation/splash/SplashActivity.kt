package kr.techit.lion.presentation.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kr.techit.lion.domain.model.Activation
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivitySplashBinding
import kr.techit.lion.presentation.delegate.NetworkEvent
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.onboarding.OnBoardingActivity
import kr.techit.lion.presentation.main.MainActivity
import kr.techit.lion.presentation.splash.vm.SplashViewModel
import androidx.core.net.toUri
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()

        repeatOnStarted {
            launch { collectActivationState() }
            launch { collectNetworkEvent() }
        }
    }

    private fun initView() {
        val videoPath = "android.resource://" + packageName + "/" + R.raw.splash_video
        with(binding.splashVideoView) {

            setVideoURI(videoPath.toUri())

            setOnPreparedListener { mp ->
                val videoWidth = mp.videoWidth.toFloat()
                val videoHeight = mp.videoHeight.toFloat()
                val videoAspectRatio = videoWidth / videoHeight

                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val screenWidth = displayMetrics.widthPixels.toFloat()
                val screenHeight = displayMetrics.heightPixels.toFloat()

                val screenAspectRatio = screenWidth / screenHeight

                val layoutParams = this.layoutParams

                if (videoAspectRatio > screenAspectRatio) {
                    layoutParams.width = screenWidth.toInt()
                    layoutParams.height = (screenWidth / videoAspectRatio).toInt()
                } else {
                    layoutParams.width = screenWidth.toInt()
                    layoutParams.height = (screenWidth / videoAspectRatio).toInt()
                }
                this.layoutParams = layoutParams

                this.start()
            }
        }
    }

    private suspend fun collectActivationState() {
        viewModel.userActivationState.collect {
            when (it) {
                Activation.Loading -> Unit
                Activation.Activate -> {
                    delay(DELAY_FOR_DISPLAY_SPLASH_ANIMATION)
                    moveToMain()
                }

                Activation.DeActivate -> viewModel.loadAreaCode()
            }
        }
    }

    private fun moveToMain() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

    private suspend fun collectNetworkEvent() {
        viewModel.networkEvent.collect { event ->
            when (event) {
                NetworkEvent.Loading -> Unit
                NetworkEvent.Success -> moveToOnBoarding()
                is NetworkEvent.Error -> showInfinitySnackBar(binding.root, event.msg)
            }
        }
    }

    private fun moveToOnBoarding() {
        startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
        finish()
    }

    companion object {
        private const val DELAY_FOR_DISPLAY_SPLASH_ANIMATION = 2700L
    }
}
