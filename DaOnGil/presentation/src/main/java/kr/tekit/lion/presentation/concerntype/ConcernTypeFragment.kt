package kr.tekit.lion.presentation.concerntype

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.concerntype.vm.ConcernTypeViewModel
import kr.tekit.lion.presentation.databinding.FragmentConcernTypeBinding

@AndroidEntryPoint
class ConcernTypeFragment : Fragment(R.layout.fragment_concern_type) {

    private val viewModel: ConcernTypeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentConcernTypeBinding.bind(view)

        val nickName = requireActivity().intent.getStringExtra("nickName")

        initView(binding, nickName)
        observeSelection(binding)
        moveConcernTypeModify(binding)
    }

    private fun initView(binding: FragmentConcernTypeBinding, nickName: String?) {
        with(binding) {
            toolbarConcernType.setNavigationIcon(R.drawable.back_icon)
            toolbarConcernType.setNavigationOnClickListener {
                requireActivity().finish()
            }

            textViewConcernTypeUseNickname.text = nickName ?: ""
        }
    }

    private fun observeSelection(binding: FragmentConcernTypeBinding) {
//        viewModel.concernType.observe(viewLifecycleOwner) { concernType ->
//            initSelection(binding, concernType)
//        }
    }

    private fun initSelection(binding: FragmentConcernTypeBinding, concernType: ConcernType) {
        with(binding) {
            if (concernType.isPhysical) {
                settingSelected(imageViewConcernTypePhysical, R.drawable.cc_selected_physical_disability_icon)
            }
            if (concernType.isVisual) {
                settingSelected(imageViewConcernTypeVisual, R.drawable.cc_selected_visual_impairment_icon)
            }
            if (concernType.isHear) {
                settingSelected(imageViewConcernTypeHearing, R.drawable.cc_selected_hearing_impairment_icon)
            }
            if (concernType.isChild) {
                settingSelected(imageViewConcernTypeInfant, R.drawable.cc_selected_infant_family_icon)
            }
            if (concernType.isElderly) {
                settingSelected(imageViewConcernTypeElderly, R.drawable.cc_selected_elderly_people_icon)
            }
        }
    }

    private fun settingSelected(imageView: ImageView, selectedDrawable: Int) {
        imageView.setImageResource(selectedDrawable)
    }

    private fun moveConcernTypeModify(binding: FragmentConcernTypeBinding) {
        binding.buttonConcernType.setOnClickListener {
            findNavController().navigate(R.id.action_concernTypeFragment_to_concernTypeModifyFragment)
        }
    }
}