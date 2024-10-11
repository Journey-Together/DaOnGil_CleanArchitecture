package kr.techit.lion.presentation.emergency

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityEmergencyInfoBinding
import kr.techit.lion.presentation.emergency.fragment.AedInfoFragment
import kr.techit.lion.presentation.emergency.fragment.EmergencyInfoFragment
import kr.techit.lion.presentation.emergency.fragment.PharmacyInfoFragment
import kr.techit.lion.presentation.emergency.vm.EmergencyInfoViewModel
import kr.techit.lion.presentation.model.EmergencyInfo
import kr.techit.lion.presentation.model.PharmacyInfo

@AndroidEntryPoint
class EmergencyInfoActivity : AppCompatActivity() {
    private val binding: ActivityEmergencyInfoBinding by lazy {
        ActivityEmergencyInfoBinding.inflate(layoutInflater)
    }

    private val viewModel: EmergencyInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val infoType = intent.getStringExtra("infoType")
        val data = if (infoType == "pharmacy") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("data", PharmacyInfo::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<PharmacyInfo>("data")
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("data", EmergencyInfo::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<EmergencyInfo>("data")
            }
        }

        if (infoType == "hospital") {
            (data as? EmergencyInfo)?.emergencyId?.let { viewModel.getEmergencyMessage(it) }
        }

        replaceFragment()
    }

    private fun replaceFragment(){

        val name = intent.getStringExtra("infoType")
        val fragmentManager = supportFragmentManager.beginTransaction()

        when(name) {
            "hospital" -> fragmentManager.replace(R.id.emergency_info_container, EmergencyInfoFragment())
            "aed" -> fragmentManager.replace(R.id.emergency_info_container, AedInfoFragment())
            "pharmacy" -> fragmentManager.replace(R.id.emergency_info_container, PharmacyInfoFragment())
        }

        fragmentManager.commit()
    }
}