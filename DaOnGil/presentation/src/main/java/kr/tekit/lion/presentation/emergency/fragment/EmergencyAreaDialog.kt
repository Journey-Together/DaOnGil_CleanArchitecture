package kr.tekit.lion.presentation.emergency.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.ArrayRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.DialogEmergencyAreaBinding

class EmergencyAreaDialog(

) : DialogFragment(R.layout.dialog_emergency_area) {

    // private val viewModel: EmergencyMapViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = DialogEmergencyAreaBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(binding) {
            emergencyAreaNegative.setOnClickListener {
                emergencyAreaSelected.setText("")
                emergencyDetailAreaSelected.setText("")
                dismiss()
            }

            emergencyAreaPositive.setOnClickListener {
                val areaDetail =
                    if (emergencyDetailAreaSelected.text.toString() == "세종특별자치시") null else emergencyDetailAreaSelected.text.toString()
                // viewModel.setArea(emergencyAreaSelected.text.toString(), areaDetail)
                emergencyAreaSelected.setText("")
                emergencyDetailAreaSelected.setText("")
                dismiss()
            }

            val areas = resources.getStringArray(R.array.areas)

            val areaAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                areas
            )

            emergencyAreaSelected.setAdapter(areaAdapter)

            emergencyAreaSelected.setOnItemClickListener { adapterView, view, position, id ->
                val selectedArea = adapterView.getItemAtPosition(position).toString()
                // 선택된 지역의 영문 리소스 이름을 매핑합니다.
                val areaNameInEnglish = mapAreaNameToEnglish(selectedArea)

                @ArrayRes
                val detailAreasResourceId = resources.getIdentifier(
                    "area_details_$areaNameInEnglish",
                    "array",
                    context?.packageName
                )

                val detailAreas = resources.getStringArray(detailAreasResourceId)

                val areaDetailAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    detailAreas
                )
                emergencyDetailAreaSelected.setAdapter(areaDetailAdapter)
                emergencyDetailAreaSelected.setText("")
                emergencyAreaPositive.isEnabled = false
                emergencyDetailAreaLayout.visibility = View.VISIBLE
            }

            emergencyDetailAreaSelected.setOnItemClickListener { adapterView, view, i, l ->
                emergencyAreaPositive.isEnabled = true
            }

        }
    }

    private fun mapAreaNameToEnglish(areaName: String): String {
        return when (areaName) {
            "서울특별시" -> "seoul"
            "부산광역시" -> "busan"
            "대구광역시" -> "daegu"
            "인천광역시" -> "incheon"
            "광주광역시" -> "gwangju"
            "대전광역시" -> "daejeon"
            "울산광역시" -> "ulsan"
            "세종특별자치시" -> "sejong"
            "경기도" -> "gyeonggi"
            "강원특별자치도" -> "gangwon"
            "충청북도" -> "chungbuk"
            "충청남도" -> "chungnam"
            "전북특별자치도" -> "jeonbuk"
            "전라남도" -> "jeonnam"
            "경상북도" -> "gyeongbuk"
            "경상남도" -> "gyeongnam"
            "제주특별자치도" -> "jeju"
            else -> ""
        }
    }
}