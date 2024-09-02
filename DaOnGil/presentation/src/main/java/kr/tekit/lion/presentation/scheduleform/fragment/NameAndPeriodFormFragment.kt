package kr.tekit.lion.presentation.scheduleform.fragment

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.FragmentNameAndPeriodFormBinding
import kotlin.concurrent.thread

class NameAndPeriodFormFragment : Fragment(R.layout.fragment_name_and_period_form) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNameAndPeriodFormBinding.bind(view)

        initToolbar(binding)
        initView(binding)
        setButtonClickListener(binding)

        showSoftInput(binding.editNpfName)
    }

    private fun initToolbar(binding: FragmentNameAndPeriodFormBinding) {
        binding.toolbarNpf.setNavigationOnClickListener {
            requireActivity().setResult(Activity.RESULT_CANCELED)
            requireActivity().finish()
        }
    }

    private fun initView(binding: FragmentNameAndPeriodFormBinding){
        with (binding){
            editNpfName.addTextChangedListener {
                clearErrorMessage(textInputNpfName)
            }
        }
    }

    private fun setButtonClickListener(binding: FragmentNameAndPeriodFormBinding) {
        binding.apply {
            buttonNpfSetPeriod.setOnClickListener {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTheme(R.style.DateRangePickerTheme)
                    .setTitleText("여행 기간을 설정해주세요")
                    .build()

                dateRangePicker.show(
                    requireActivity().supportFragmentManager,
                    "ScheduleFormFragment_ButtonSetPeriod"
                )
                dateRangePicker.addOnPositiveButtonClickListener {
                    // viewModel에 시작일, 종료일 데이터 전달
//                scheduleFormViewModel.setStartDate(Date(it.first))
//                scheduleFormViewModel.setEndDate(Date(it.second))
                    showPickedDates(binding)
                }
            }

            buttonNpfNextStep.setOnClickListener {
                val isNameAndPeriodValidate = validateScheduleNameAndPeriod(this)
                if (isNameAndPeriodValidate) {
//                    scheduleFormViewModel.setTitle(editTextNPFName.text.toString())

                    val navController = findNavController()
                    val action = NameAndPeriodFormFragmentDirections.toScheduleDetailsFormFragment()
                    navController.navigate(action)
                }
            }
        }
    }

    private fun showPickedDates(binding: FragmentNameAndPeriodFormBinding) {
//        val startDate = scheduleFormViewModel.startDate.value
//        val endDate = scheduleFormViewModel.endDate.value
//        val startDateFormatted = startDate?.let {
//            formatDateValue(startDate)
//        }
//        val endDateFormatted = endDate?.let {
//            formatDateValue(endDate)
//        }
//        binding.buttonNPFSetPeriod.text =
//            getString(R.string.picked_dates, startDateFormatted, endDateFormatted)
    }

    private fun validateScheduleNameAndPeriod(binding: FragmentNameAndPeriodFormBinding): Boolean {
        with(binding) {
            editNpfName.apply {
                val tempName = this.text.toString()

                if (tempName.isEmpty()) {
                    textInputNpfName.error = "제목은 1글자 이상 입력해주세요"
                    return false
                }
            }

            //val hasStartDate = scheduleFormViewModel.hasStartDate()

//            if (!hasStartDate) {
//                buttonNpfSetPeriod.showSnackbar("여행 기간을 설정해주세요", Snackbar.LENGTH_LONG)
//                return false
//            }
        }

        return true
    }

    private fun showSoftInput(view: View) {
        view.requestFocus()

        thread {
            SystemClock.sleep(400)
            val inputMethodManager =
                requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, 0)
        }
    }

    private fun clearErrorMessage(textInputLayout: TextInputLayout){
        textInputLayout.error = null
    }
}