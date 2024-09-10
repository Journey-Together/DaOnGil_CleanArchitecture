package kr.tekit.lion.presentation.report

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityReportBinding
import kr.tekit.lion.presentation.delegate.NetworkState
import kr.tekit.lion.presentation.ext.repeatOnStarted
import kr.tekit.lion.presentation.ext.showSoftInput
import kr.tekit.lion.presentation.report.vm.ReportViewModel

@AndroidEntryPoint
class ReportActivity : AppCompatActivity() {

    private val binding: ActivityReportBinding by lazy {
        ActivityReportBinding.inflate(layoutInflater)
    }

    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        repeatOnStarted {
            viewModel.networkState.collect { networkState ->
                when (networkState) {
                    is NetworkState.Success -> {
                        setResult(RESULT_OK)
                        finish()
                    }
                    is NetworkState.Loading -> {
                    }
                }
            }
        }

        settingToolbar()
        settingReason()
        settingReportButton()
        settingErrorHandling()
    }

    private fun settingToolbar() {
        binding.toolbarReport.setNavigationOnClickListener {
            finish()
        }
    }

    private fun settingReason() {
        with(binding) {
            radioGroupReport.setOnCheckedChangeListener { _, checkedId ->
                val selectedReason = when (checkedId) {
                    R.id.radioButtonPirate -> getString(R.string.text_report_reason_pirate)
                    R.id.radioButtonCurse -> getString(R.string.text_report_reason_curse)
                    R.id.radioButtonPromote -> getString(R.string.text_report_reason_promote)
                    R.id.radioButtonViolence -> getString(R.string.text_report_reason_violence)
                    R.id.radioButtonWrong -> getString(R.string.text_report_reason_wrong)
                    else -> getString(R.string.text_report_reason_etc)
                }
                viewModel.setSelectedReason(selectedReason)
            }


            textFieldDetailReason.addTextChangedListener { detailReason ->
                viewModel.setDetailedReason(detailReason.toString())
            }
        }
    }

    private fun settingReportButton() {
        with(binding) {
            buttonReport.setOnClickListener {
                if (radioGroupReport.checkedRadioButtonId == R.id.radioButtonEtc) {
                    if (isFormValid()) {
                        viewModel.submitReportReview()
//                        setResult(RESULT_OK)
//                        finish()
                    }
                } else {
                    viewModel.submitReportReview()
//                    setResult(RESULT_OK)
//                    finish()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        return if (binding.textFieldDetailReason.text.isNullOrBlank()) {
            binding.textInputLayoutDetailReason.error = getString(R.string.text_report_error_message)
            binding.textFieldDetailReason.requestFocus()
            this@ReportActivity.showSoftInput(binding.textFieldDetailReason)
            false
        } else {
            true
        }
    }

    private fun settingErrorHandling() {
        binding.textFieldDetailReason.addTextChangedListener {
            clearErrorMessage(binding.textInputLayoutDetailReason)
        }
    }

    private fun clearErrorMessage(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.button_quaternary)
    }
}