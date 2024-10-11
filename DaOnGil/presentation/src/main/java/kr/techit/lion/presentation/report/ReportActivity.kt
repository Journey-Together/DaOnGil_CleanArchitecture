package kr.techit.lion.presentation.report

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ActivityReportBinding
import kr.techit.lion.presentation.delegate.NetworkState
import kr.techit.lion.presentation.ext.repeatOnStarted
import kr.techit.lion.presentation.ext.showInfinitySnackBar
import kr.techit.lion.presentation.ext.showSnackbar
import kr.techit.lion.presentation.ext.showSoftInput
import kr.techit.lion.presentation.observer.ConnectivityObserver
import kr.techit.lion.presentation.observer.NetworkConnectivityObserver
import kr.techit.lion.presentation.report.vm.ReportViewModel

@AndroidEntryPoint
class ReportActivity : AppCompatActivity() {

    private val binding: ActivityReportBinding by lazy {
        ActivityReportBinding.inflate(layoutInflater)
    }
    private val viewModel: ReportViewModel by viewModels()
    private val connectivityObserver: ConnectivityObserver by lazy {
        NetworkConnectivityObserver.getInstance(this@ReportActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        settingToolbar()
        settingReason()
        settingReportButton()
        settingErrorHandling()

        repeatOnStarted {
            supervisorScope {
                launch { collectReportState() }
                launch { observeConnectivity() }
            }
        }
    }

    private suspend fun collectReportState() {
        viewModel.networkState.collect { networkState ->
            when (networkState) {
                is NetworkState.Loading -> {
                }
                is NetworkState.Success -> {
                    setResult(RESULT_OK)
                    finish()
                }
                is NetworkState.Error -> {
                    this@ReportActivity.showSnackbar(binding.root, networkState.msg)
                }
            }
        }
    }

    private suspend fun observeConnectivity() {
        with(binding) {
            connectivityObserver.getFlow().collect { connectivity ->
                when(connectivity) {
                    ConnectivityObserver.Status.Available -> {
                        buttonReport.isEnabled = true
                    }
                    ConnectivityObserver.Status.Unavailable -> {}
                    ConnectivityObserver.Status.Losing,
                    ConnectivityObserver.Status.Lost -> {
                        buttonReport.isEnabled = false
                        val msg = "${getString(R.string.text_network_is_unavailable)}\n" +
                                "${getString(R.string.text_plz_check_network)} "
                        this@ReportActivity.showInfinitySnackBar(buttonReport, msg)
                    }
                }
            }
        }
    }

    private fun settingToolbar() {
        binding.toolbarReport.setNavigationOnClickListener {
            finish()
        }

        binding.toolbarReport.setNavigationContentDescription(R.string.text_back_button)
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
                    }
                } else {
                    viewModel.submitReportReview()
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