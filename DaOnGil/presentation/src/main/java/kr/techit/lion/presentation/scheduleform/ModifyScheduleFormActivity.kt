package kr.techit.lion.presentation.scheduleform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.databinding.ActivityModifyScheduleFormBinding

@AndroidEntryPoint
class ModifyScheduleFormActivity : AppCompatActivity() {
    private val binding: ActivityModifyScheduleFormBinding by lazy {
        ActivityModifyScheduleFormBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
    }
}