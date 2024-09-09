package kr.tekit.lion.presentation.report

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ActivityReportBinding
import kr.tekit.lion.presentation.ext.showSoftInput

@AndroidEntryPoint
class ReportActivity : AppCompatActivity() {

    private val binding: ActivityReportBinding by lazy {
        ActivityReportBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settingToolbar()
        settingReportButton()
        settingErrorHandling()
    }

    private fun settingToolbar() {
        binding.toolbarReport.setNavigationOnClickListener {
            finish()
        }
    }

    private fun settingReportButton() {
        with(binding) {
            buttonReport.setOnClickListener {
                if (radioGroupReport.checkedRadioButtonId == R.id.radioButtonEtc) {
                    if (isFormValid()) {
                        setResult(RESULT_OK)
                        finish()
                    }
                } else {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        return if (binding.textFieldEtc.text.isNullOrBlank()) {
            binding.textInputLayoutEtc.error = getString(R.string.text_report_error_message)
            binding.textFieldEtc.requestFocus()
            this@ReportActivity.showSoftInput(binding.textFieldEtc)
            false
        } else {
            true
        }
    }

    private fun settingErrorHandling() {
        binding.textFieldEtc.addTextChangedListener {
            clearErrorMessage(binding.textInputLayoutEtc)
        }
    }

    private fun clearErrorMessage(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.button_quaternary)
    }
}