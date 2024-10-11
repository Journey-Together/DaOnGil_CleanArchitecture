package kr.techit.lion.presentation.emergency.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentPharmacyInfoBinding
import kr.techit.lion.presentation.model.PharmacyInfo

class PharmacyInfoFragment : Fragment(R.layout.fragment_pharmacy_info) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPharmacyInfoBinding.bind(view)

        binding.toolbarPharmcyInfo.setNavigationOnClickListener {
            requireActivity().finish()
        }
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().intent.getParcelableExtra("data", PharmacyInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireActivity().intent.getParcelableExtra("data")
        }

        with(binding){
            pharmacyName.text = data?.pharmacyName
            pharmacyAddress.text = data?.pharmacyAddress
            pharmLocationText.text = data?.pharmacyLocation
            pharmMonTime.text = data?.monTime
            pharmTueTime.text = data?.tueTime
            pharmWedTime.text = data?.wedTime
            pharmThuTime.text = data?.thuTime
            pharmFriTime.text = data?.friTime
            pharmSatTime.text = data?.satTime
            pharmSunTime.text = data?.sunTime
            pharmOffTime.text = data?.holTime

            mainPharmacyCall.setOnClickListener {
                val phoneNumber = data?.pharmacyTel
                if (!phoneNumber.isNullOrBlank() || !phoneNumber.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                    startActivity(intent)
                } else {
                    showSnackbar(this, "전화번호가 존재하지 않습니다.")
                }
            }

        }
    }

    private fun showSnackbar(binding: FragmentPharmacyInfoBinding, message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            .show()
    }
}