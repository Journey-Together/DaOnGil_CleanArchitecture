package kr.tekit.lion.presentation.splash

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivitySplashBinding
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.login.LoginActivity
import kr.tekit.lion.presentation.main.MainActivity
import kr.tekit.lion.presentation.splash.model.LogInState
import kr.tekit.lion.presentation.splash.vm.SplashViewModel

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
        with(binding.splashVideoView){

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
                repeatOnStarted {
                    viewModel.logInState.collect { state ->
                        when (state) {
                            is LogInState.LoggedIn -> {
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }

                            is LogInState.LoginRequired -> {
                                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                finish()
                            }

                            is LogInState.Checking -> {
                                return@collect
                            }
                        }
                    }
                }
            }
        }
    }
}