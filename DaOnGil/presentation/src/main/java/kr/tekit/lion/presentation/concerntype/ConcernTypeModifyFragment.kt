package kr.tekit.lion.presentation.concerntype

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kr.tekit.lion.domain.model.ConcernType
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.concerntype.vm.ConcernTypeViewModel
import kr.tekit.lion.presentation.databinding.FragmentConcernTypeModifyBinding

class ConcernTypeModifyFragment : Fragment(R.layout.fragment_concern_type_modify) {

    private val viewModel: ConcernTypeViewModel by activityViewModels()

    private val selectedConcernType = mutableSetOf<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentConcernTypeModifyBinding.bind(view)

        initView(binding)
        observeSelection(binding)
        concernTypeModify(binding)
    }

    private fun initView(binding: FragmentConcernTypeModifyBinding) {
        with(binding) {
            toolbarConcernTypeModify.setNavigationIcon(R.drawable.back_icon)
            toolbarConcernTypeModify.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            imageViewConcernTypeModifyPhysical.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.physical_no_select, R.drawable.physical_select, binding)
            }
            imageViewConcernTypeModifyVisual.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.visual_no_select, R.drawable.visual_select, binding)
            }
            imageViewConcernTypeModifyHearing.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.hearing_no_select, R.drawable.hearing_select, binding)
            }
            imageViewConcernTypeModifyInfant.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.infant_family_no_select, R.drawable.infant_family_select, binding)
            }
            imageViewConcernTypeModifyElderly.setOnClickListener {
                toggleSelection(it as ImageView, R.drawable.elderly_people_no_select, R.drawable.elderly_people_select, binding)
            }
        }
    }

    private fun observeSelection(binding: FragmentConcernTypeModifyBinding) {
//        viewModel.concernType.observe(viewLifecycleOwner) { concernType ->
//            initSelection(binding, concernType)
//        }
    }

    private fun initSelection(binding: FragmentConcernTypeModifyBinding, concernType: ConcernType) {
        with(binding) {
            if (concernType.isPhysical) {
                settingSelected(imageViewConcernTypeModifyPhysical, R.drawable.physical_select)
            }
            if (concernType.isVisual) {
                settingSelected(imageViewConcernTypeModifyVisual, R.drawable.visual_select)
            }
            if (concernType.isHear) {
                settingSelected(imageViewConcernTypeModifyHearing, R.drawable.hearing_select)
            }
            if (concernType.isChild) {
                settingSelected(imageViewConcernTypeModifyInfant, R.drawable.infant_family_select)
            }
            if (concernType.isElderly) {
                settingSelected(imageViewConcernTypeModifyElderly, R.drawable.elderly_people_select)
            }
        }

        updateModifyButtonState(binding)
    }

    private fun settingSelected(imageView: ImageView, selectedDrawable: Int) {
        imageView.setImageResource(selectedDrawable)
        imageView.tag = "true"
        selectedConcernType.add(imageView.id)
    }

    private fun toggleSelection(imageView: ImageView, unselectedDrawable: Int, selectedDrawable: Int, binding: FragmentConcernTypeModifyBinding) {
        if (imageView.tag == "true") {
            imageView.setImageResource(unselectedDrawable)
            imageView.tag = "false"
            selectedConcernType.remove(imageView.id)
        } else {
            imageView.setImageResource(selectedDrawable)
            imageView.tag = "true"
            selectedConcernType.add(imageView.id)
        }

        updateModifyButtonState(binding)
    }

    private fun updateModifyButtonState(binding: FragmentConcernTypeModifyBinding) {
        binding.buttonConcernTypeModify.isEnabled = selectedConcernType.isNotEmpty()
    }

    private fun concernTypeModify(binding: FragmentConcernTypeModifyBinding) {
        binding.buttonConcernTypeModify.setOnClickListener {
            val isPhysical = binding.imageViewConcernTypeModifyPhysical.tag.toString().toBoolean()
            val isHear = binding.imageViewConcernTypeModifyHearing.tag.toString().toBoolean()
            val isVisual = binding.imageViewConcernTypeModifyVisual.tag.toString().toBoolean()
            val isElderly = binding.imageViewConcernTypeModifyElderly.tag.toString().toBoolean()
            val isChild = binding.imageViewConcernTypeModifyInfant.tag.toString().toBoolean()

            //viewModel.updateConcernType(ConcernType(isPhysical, isHear, isVisual, isElderly, isChild))
            showSnackbar(binding, "관심 유형이 수정되었습니다.")
            findNavController().popBackStack()
        }
    }

    private fun showSnackbar(binding: FragmentConcernTypeModifyBinding, message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            .show()
    }
}