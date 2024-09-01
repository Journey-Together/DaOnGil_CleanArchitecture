package kr.tekit.lion.presentation.emergency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.databinding.ActivityEmergencyMapBinding
import kr.tekit.lion.presentation.emergency.vm.EmergencyMapViewModel

@AndroidEntryPoint
class EmergencyMapActivity : AppCompatActivity() {

    private val binding: ActivityEmergencyMapBinding by lazy {
        ActivityEmergencyMapBinding.inflate(layoutInflater)
    }

    private val viewModel: EmergencyMapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}