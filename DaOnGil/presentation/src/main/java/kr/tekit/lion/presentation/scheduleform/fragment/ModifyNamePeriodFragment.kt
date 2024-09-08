package kr.tekit.lion.presentation.scheduleform.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentModifyNamePeriodBinding
import kr.tekit.lion.presentation.ext.showSnackbar
import kr.tekit.lion.presentation.scheduleform.model.OriginalScheduleInfo
import kr.tekit.lion.presentation.scheduleform.vm.ModifyScheduleFormViewModel
import java.util.Date

@AndroidEntryPoint
class ModifyNamePeriodFragment : Fragment(R.layout.fragment_modify_name_period) {

    private val viewModel: ModifyScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyNamePeriodBinding.bind(view)

        initToolbar(binding)
        initScheduleData(binding)
        initButtonClickListener(binding)

    }

    private fun initToolbar(binding: FragmentModifyNamePeriodBinding){
        binding.toolbarModifyNpf.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initScheduleData(binding: FragmentModifyNamePeriodBinding) {
        val originalScheduleInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().intent.getParcelableExtra(
                "scheduleInfo",
                OriginalScheduleInfo::class.java
            )
        } else {
            requireActivity().intent.getParcelableExtra("scheduleInfo") as? OriginalScheduleInfo
        }

        originalScheduleInfo?.let {
            viewModel.initScheduleDetailInfo(it)
        }

        initScheduleTitleAndPeriod(binding)
    }

    private fun initScheduleTitleAndPeriod(binding: FragmentModifyNamePeriodBinding) {
        viewModel.title.observe(viewLifecycleOwner) {
            binding.editModiyNpfTitle.setText(it)
        }
        viewModel.endDate.observe(viewLifecycleOwner) {
            val pickedDates = viewModel.formatPickedDates()
            binding.buttonModifyNpfSetPeriod.text = pickedDates
        }
    }

    private fun initButtonClickListener(binding: FragmentModifyNamePeriodBinding) {
        with(binding) {
            buttonModifyNpfSetPeriod.setOnClickListener {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTheme(R.style.DateRangePickerTheme)
                    .setTitleText("조회기간을 설정해주세요")
                    .build()

                dateRangePicker.show(requireActivity().supportFragmentManager, "ModifyScheduleFormSetPeriod")
                dateRangePicker.addOnPositiveButtonClickListener {
                    viewModel.setStartDate(Date(it.first))
                    viewModel.setEndDate(Date(it.second))
                }
            }

            buttonModifyNpfNextStep.setOnClickListener { view ->
                val isNameAndPeriodValidate = validateScheduleNameAndPeriod(this)

                if(isNameAndPeriodValidate){
                    viewModel.setTitle(editModiyNpfTitle.text.toString())

                    val navController = findNavController()
                    val action = ModifyNamePeriodFragmentDirections.toModifyScheduleDetailsFragment()
                    navController.navigate(action)
                }
            }
        }
    }

    private fun validateScheduleNameAndPeriod(binding: FragmentModifyNamePeriodBinding): Boolean {
        with(binding) {
            editModiyNpfTitle.apply {
                val tempName = this.text.toString()

                if (tempName.isEmpty()) {
                    textInputModifyNpfTitle.error = "제목은 1글자 이상 입력해주세요"
                    return false
                }
            }

            val hasStartDate = viewModel.hasStartDate()

            if (!hasStartDate) {
                buttonModifyNpfSetPeriod.showSnackbar("여행 기간을 설정해주세요")
                return false
            }
        }

        return true
    }

}