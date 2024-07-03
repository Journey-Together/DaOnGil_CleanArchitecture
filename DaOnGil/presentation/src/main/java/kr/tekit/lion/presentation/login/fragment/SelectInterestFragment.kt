package kr.tekit.lion.presentation.login.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentSelectInterestBinding
import kr.tekit.lion.presentation.main.MainActivity

class SelectInterestFragment : Fragment(R.layout.fragment_select_interest) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSelectInterestBinding.bind(view)

        // NavController 한 번만 찾기
        val navController = findNavController()

        // 전달받은 Bundle 데이터 받기
        val loginType = arguments?.getString("loginType")

        // loginType에 따라 필요한 작업 수행
        loginType?.let {
            when (it) {
                "kakao" -> {
                    // 카카오 로그인 처리
                }

                "naver" -> {
                    // 네이버 로그인 처리
                }
            }
        }

        val interestImageViews = listOf(
            binding.physicalDisabilityImageView,
            binding.visualImpairmentImageView,
            binding.hearingImpairmentImageView,
            binding.infantFamilyImageView,
            binding.elderlyPeopleImageView
        )

        interestImageViews.forEach { imageView ->
            imageView.setOnClickListener { selectInterest(binding, it as ImageView) }
        }

        binding.selectInterestCompleteButton.isEnabled = false

        binding.selectInterestCompleteButton.setOnClickListener {
            val selectedInterests = getSelectedInterests(binding)

            val intent = Intent(requireActivity(), MainActivity::class.java).apply {
                putExtra("selectedInterest", ArrayList(selectedInterests))
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private val selectedStates = hashMapOf<Int, Boolean>().apply {
        put(R.id.physicalDisabilityImageView, false)
        put(R.id.visualImpairmentImageView, false)
        put(R.id.hearingImpairmentImageView, false)
        put(R.id.infantFamilyImageView, false)
        put(R.id.elderlyPeopleImageView, false)
    }

    private fun selectInterest(binding: FragmentSelectInterestBinding, selectedImageView: ImageView) {
        // 현재 선택 상태를 토글
        val isSelected = selectedStates[selectedImageView.id] ?: false
        selectedStates[selectedImageView.id] = !isSelected

        // 선택된 이미지 뷰 상태에 따라 이미지 변경
        when (selectedImageView.id) {
            R.id.physicalDisabilityImageView -> {
                selectedImageView.setImageResource(if (!isSelected) R.drawable.physical_select else R.drawable.physical_no_select)
            }

            R.id.visualImpairmentImageView -> {
                selectedImageView.setImageResource(if (!isSelected) R.drawable.visual_select else R.drawable.visual_no_select)
            }

            R.id.hearingImpairmentImageView -> {
                selectedImageView.setImageResource(if (!isSelected) R.drawable.hearing_select else R.drawable.hearing_no_select)
            }

            R.id.infantFamilyImageView -> {
                selectedImageView.setImageResource(if (!isSelected) R.drawable.infant_family_select else R.drawable.infant_family_no_select)
            }

            R.id.elderlyPeopleImageView -> {
                selectedImageView.setImageResource(if (!isSelected) R.drawable.elderly_people_select else R.drawable.elderly_people_no_select)
            }
        }
        val anySelected = selectedStates.values.any { it }
        binding.selectInterestCompleteButton.isEnabled = anySelected
    }

    private fun getSelectedInterests(binding: FragmentSelectInterestBinding): List<String> {
        val selectedInterests = mutableListOf<String>()

        selectedStates.forEach { _, _ ->
            with(binding) {
                if (physicalDisabilityImageView.tag == true) {
                    selectedInterests.add("physicalDisability")
                }
                if (visualImpairmentImageView.tag == true) {
                    selectedInterests.add("visualImpairment")
                }
                if (hearingImpairmentImageView.tag == true) {
                    selectedInterests.add("hearingImpairment")
                }
                if (infantFamilyImageView.tag == true) {
                    selectedInterests.add("infantFamily")
                }
                if (elderlyPeopleImageView.tag == true) {
                    selectedInterests.add("elderlyPeople")
                }
            }
        }
        return selectedInterests
    }
}