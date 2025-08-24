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

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val videoPath = "android.resource://" + packageName + "/" + R.raw.splash_video
        with(binding.splashVideoView) {

            setVideoURI(Uri.parse(videoPath))

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

            repeatOnStarted {
                viewModel.userActivationState.collect {
                    when (it) {
                        Activation.Activate -> {
                            delay(DELAY_FOR_DISPLAY_SPLASH_ANIMATION)
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }

                        Activation.DeActivate -> {
                            viewModel.whenUserActivationIsDeActivate()
                        }
                        Activation.Loading -> Unit
                    }
                }
            }

            repeatOnStarted {
                viewModel.networkEvent.collect { event ->
                    when (event) {
                        NetworkEvent.Loading -> Unit
                        NetworkEvent.Success -> {
                            startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
                            finish()
                        }
                        is NetworkEvent.Error -> {
                            showInfinitySnackBar(binding.root, event.msg)
                        }
                    }
                }
            }
        }
    }
    companion object{
        private const val DELAY_FOR_DISPLAY_SPLASH_ANIMATION = 2700L
    }
}
