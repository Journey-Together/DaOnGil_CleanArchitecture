package kr.tekit.lion.presentation.myschedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.tekit.lion.presentation.databinding.ActivityMyScheduleBinding

class MyScheduleActivity : AppCompatActivity() {
    private val binding: ActivityMyScheduleBinding by lazy {
        ActivityMyScheduleBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

    }
}