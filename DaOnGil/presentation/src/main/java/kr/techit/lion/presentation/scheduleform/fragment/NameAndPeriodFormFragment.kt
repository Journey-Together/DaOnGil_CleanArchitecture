package kr.techit.lion.presentation.scheduleform.fragment

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.FragmentNameAndPeriodFormBinding
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.scheduleform.FormDateFormat
import kr.techit.lion.presentation.scheduleform.vm.ScheduleFormViewModel
import java.util.Date
import kotlin.concurrent.thread

@AndroidEntryPoint
class NameAndPeriodFormFragment : Fragment(R.layout.fragment_name_and_period_form) {

    private val scheduleFormViewModel: ScheduleFormViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentNameAndPeriodFormBinding.bind(view)

        initToolbar(binding)
        initView(binding)
        setButtonClickListener(binding)

        showSoftInput(binding.editNpfTitle)
    }

    private fun initToolbar(binding: FragmentNameAndPeriodFormBinding) {
        binding.toolbarNpf.setNavigationOnClickListener {
            requireActivity().setResult(Activity.RESULT_CANCELED)
            requireActivity().finish()
        }
    }

    private fun initView(binding: FragmentNameAndPeriodFormBinding) {
        with(binding) {
            // 이미 제목, 여행 기간이 저장된 경우
            scheduleFormViewModel.getScheduleTitle().let { editNpfTitle.text }
            val isDatesAvailable = scheduleFormViewModel.hasDates()
            if(isDatesAvailable) showPickedDates(binding)

            editNpfTitle.addTextChangedListener {
                clearErrorMessage(textInputNpfTitle)
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
                    scheduleFormViewModel.setStartDate(Date(it.first))
                    scheduleFormViewModel.setEndDate(Date(it.second))
                    showPickedDates(binding)
                }
            }

            buttonNpfNextStep.setOnClickListener {
                val isNameAndPeriodValidate = validateScheduleNameAndPeriod(this)
                if (isNameAndPeriodValidate) {
                    scheduleFormViewModel.setTitle(editNpfTitle.text.toString())

                    val navController = findNavController()
                    val action = NameAndPeriodFormFragmentDirections.toScheduleDetailsFormFragment()
                    navController.navigate(action)
                }
            }
        }
    }

    private fun showPickedDates(binding: FragmentNameAndPeriodFormBinding) {
        val schedulePeriod = scheduleFormViewModel.getSchedulePeriod(FormDateFormat.YYYY_MM_DD_E)

        binding.buttonNpfSetPeriod.apply {
            setAccessibilityText(scheduleFormViewModel.getSchedulePeriodAccessibilityText())
            text = schedulePeriod
        }
    }

    private fun validateScheduleNameAndPeriod(binding: FragmentNameAndPeriodFormBinding): Boolean {
        with(binding) {
            editNpfTitle.apply {
                val tempTitle = this.text.toString()

                if (tempTitle.isEmpty()) {
                    textInputNpfTitle.error = "제목은 1글자 이상 입력해주세요"
                    return false
                }
            }

            val hasStartDate = scheduleFormViewModel.hasDates()

            if (!hasStartDate) {
                buttonNpfSetPeriod.showSnackbar("여행 기간을 설정해주세요", Snackbar.LENGTH_LONG)
                return false
            }
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

    private fun clearErrorMessage(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
    }
}