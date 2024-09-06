package kr.tekit.lion.presentation.schedulereview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.tekit.lion.presentation.databinding.ActivityModifyScheduleReviewBinding

class ModifyScheduleReviewActivity : AppCompatActivity() {

    private val binding: ActivityModifyScheduleReviewBinding by lazy {
        ActivityModifyScheduleReviewBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val planId = intent.getLongExtra("planId", -1)
    }
}