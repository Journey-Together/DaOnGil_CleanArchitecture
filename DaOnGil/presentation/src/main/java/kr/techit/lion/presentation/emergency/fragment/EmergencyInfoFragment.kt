package kr.techit.lion.presentation.emergency.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentEmergencyInfoBinding
import kr.techit.lion.presentation.emergency.adapter.EmergencyMessageAdapter
import kr.techit.lion.presentation.emergency.vm.EmergencyInfoViewModel
import kr.techit.lion.presentation.model.EmergencyInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class EmergencyInfoFragment : Fragment(R.layout.fragment_emergency_info) {

    private val viewModel: EmergencyInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEmergencyInfoBinding.bind(view)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().intent.getParcelableExtra("data", EmergencyInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireActivity().intent.getParcelableExtra("data")
        }

        with(binding){

            viewModel.messageList.observe(viewLifecycleOwner) { messageList ->

                if(messageList.size < 1) {
                    emrDefaultMessage.visibility = View.VISIBLE
                    emergencyMessageCount.text = messageList.size.toString()
                    emergencyMessageDate.text = getCurrentFormattedDateTime()

                } else {
                    emergencyMessageRV.visibility = View.VISIBLE
                    val emergencyMessageadapter =  EmergencyMessageAdapter(messageList)
                    emergencyMessageCount.text = messageList.size.toString()
                    emergencyMessageRV.adapter = emergencyMessageadapter
                }

            }

            emergencyName.text = data?.hospitalList?.hospitalName
            emergencyAddress.text = data?.hospitalList?.hospitalAddress
            emergencyUpdate.text = data?.hospitalList?.lastUpdateDate
            emergency.text = data?.hospitalList?.emergencyCount.toString() + " / " + data?.hospitalList?.emergencyAllCount.toString()
            kidEmergency.text =
                if (data?.hospitalList?.emergencyKid.isNullOrEmpty()) "운영하지 않습니다."
                else data?.hospitalList?.emergencyKidCount.toString() + " / " + data?.hospitalList?.emergencyKidAllCount.toString()
            emergencyDialysis.text = data?.hospitalList?.dialysis
            emergencyBirth.text = data?.hospitalList?.earlyBirth
            emergencyBurns.text = data?.hospitalList?.burns

            emergencyCall.setOnClickListener {
                val phoneNumber = data?.hospitalList?.emergencyTel
                if (!phoneNumber.isNullOrBlank() || !phoneNumber.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                    startActivity(intent)
                } else {
                    showSnackbar(this, "전화번호가 존재하지 않습니다.")
                }
            }

            mainEmergencyCall.setOnClickListener {
                val phoneNumber = data?.hospitalList?.hospitalTel
                if (!phoneNumber.isNullOrBlank() || !phoneNumber.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                    startActivity(intent)
                } else {
                    showSnackbar(this, "전화번호가 존재하지 않습니다.")
                }
            }

            data?.hospitalList?.emergencyBed?.let { it ->
                val emergencyKidBedIcon = binding.emergencyRadius
                val colorResId = when {
                    it >= 80 -> R.color.emergency_green
                    it >= 50 -> R.color.emergency_yellow
                    else -> R.color.emergency_red
                }
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorResId))
                emergencyKidBedIcon.setBackgroundTintList(colorStateList)
            }

            data?.hospitalList?.emergencyKidBed?.let { it ->
                val emergencyKidBedIcon = binding.kidEmergencyRadius
                val colorResId = when {
                    it >= 80 -> R.color.emergency_green
                    it >= 50 -> R.color.emergency_yellow
                    else -> R.color.emergency_red
                }
                val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorResId))
                emergencyKidBedIcon.setBackgroundTintList(colorStateList)
            }

            toolbarEmergencyInfo.setNavigationOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun getCurrentFormattedDateTime(): String {
        // 현재 시간 가져오기
        val currentDateTime = LocalDateTime.now()

        // 원하는 형식 정의
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")

        // 포맷팅된 문자열 반환
        return currentDateTime.format(formatter)
    }

    private fun showSnackbar(binding: FragmentEmergencyInfoBinding, message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            .show()
    }

}