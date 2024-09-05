package kr.tekit.lion.presentation.schedulereview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.databinding.ActivityWriteScheduleReviewBinding

@AndroidEntryPoint
class WriteScheduleReviewActivity : AppCompatActivity() {

    private val binding : ActivityWriteScheduleReviewBinding by lazy {
        ActivityWriteScheduleReviewBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val planId = intent.getLongExtra("planId", -1)

    }
}