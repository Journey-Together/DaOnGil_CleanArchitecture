package kr.tekit.lion.presentation.scheduleform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.tekit.lion.presentation.databinding.ActivityScheduleFormBinding

class ScheduleFormActivity : AppCompatActivity() {

    private val binding: ActivityScheduleFormBinding by lazy {
        ActivityScheduleFormBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
    }
}