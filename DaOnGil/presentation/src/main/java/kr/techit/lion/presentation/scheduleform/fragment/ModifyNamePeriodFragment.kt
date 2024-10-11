package kr.techit.lion.presentation.scheduleform.fragment

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentModifyNamePeriodBinding
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.scheduleform.FormDateFormat
import kr.techit.lion.presentation.scheduleform.model.OriginalScheduleInfo
import kr.techit.lion.presentation.scheduleform.vm.ModifyScheduleFormViewModel
import java.util.Date

@AndroidEntryPoint
class ModifyNamePeriodFragment : Fragment(R.layout.fragment_modify_name_period) {

    private val viewModel: ModifyScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentModifyNamePeriodBinding.bind(view)

        initToolbar(binding)
        initView(binding)
        initScheduleData(binding)
        initButtonClickListener(binding)
    }

    private fun initToolbar(binding: FragmentModifyNamePeriodBinding) {
        binding.toolbarModifyNpf.setNavigationOnClickListener {
            requireActivity().setResult(Activity.RESULT_CANCELED)
            requireActivity().finish()
        }
    }

    private fun initView(binding: FragmentModifyNamePeriodBinding) {
        with(binding) {
            editModiyNpfTitle.addTextChangedListener {
                clearErrorMessage(textInputModifyNpfTitle)
            }
        }
    }

    private fun initScheduleData(binding: FragmentModifyNamePeriodBinding) {
        if (!viewModel.hasStartDate()) {
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
        }

        initScheduleTitleAndPeriod(binding)
    }

    private fun initScheduleTitleAndPeriod(binding: FragmentModifyNamePeriodBinding) {
        viewModel.title.observe(viewLifecycleOwner) {
            binding.editModiyNpfTitle.setText(it)
        }
        viewModel.endDate.observe(viewLifecycleOwner) {
            val pickedDates = viewModel.formatPickedDates(FormDateFormat.YYYY_MM_DD_E)
            binding.buttonModifyNpfSetPeriod.apply {
                text = pickedDates
                setAccessibilityText(viewModel.getSchedulePeriodAccessibilityText())
            }
        }
    }

    private fun initButtonClickListener(binding: FragmentModifyNamePeriodBinding) {
        with(binding) {
            buttonModifyNpfSetPeriod.setOnClickListener {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTheme(R.style.DateRangePickerTheme)
                    .setTitleText("조회기간을 설정해주세요")
                    .build()

                dateRangePicker.show(
                    requireActivity().supportFragmentManager,
                    "ModifyScheduleFormSetPeriod"
                )
                dateRangePicker.addOnPositiveButtonClickListener {
                    viewModel.setStartDate(Date(it.first))
                    viewModel.setEndDate(Date(it.second))
                }
            }

            buttonModifyNpfNextStep.setOnClickListener { view ->
                val isNameAndPeriodValidate = validateScheduleTitleAndPeriod(this)

                if (isNameAndPeriodValidate) {
                    viewModel.setTitle(editModiyNpfTitle.text.toString())
                    viewModel.refreshScheduleIfPeriodChanged()

                    val navController = findNavController()
                    val action =
                        ModifyNamePeriodFragmentDirections.toModifyScheduleDetailsFragment()
                    navController.navigate(action)
                }
            }
        }
    }

    private fun validateScheduleTitleAndPeriod(binding: FragmentModifyNamePeriodBinding): Boolean {
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

    private fun clearErrorMessage(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
    }
}