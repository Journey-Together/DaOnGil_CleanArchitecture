package kr.tekit.lion.presentation.schedule.customview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.DialogReviewReportBinding

class ReviewReportDialog(private val onReviewReportReasonListener: (Int) -> Unit) :
    DialogFragment(R.layout.dialog_review_report) {

    private val REASON_TYPE_COMMERCIAL = 1
    private val REASON_TYPE_COPYRIGHT_INFRINGEMENT = 2
    private val REASON_TYPE_OBSCENITY = 3
    private val REASON_TYPE_VERBAL_ABUSE = 4
    private val REASON_TYPE_PRIVACY_EXPOSURE = 5
    private val REASON_TYPE_OTHER = 6
    private val REASON_TYPE_ERROR = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogReviewReportBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initView(binding)
    }

    private fun initView(binding: DialogReviewReportBinding) {
        binding.apply {
            buttonNegativeReviewReport.setOnClickListener {
                dismiss()
            }
            buttonPositiveReviewReport.setOnClickListener {
                val reasonType = getCheckedResonType(binding)
                reasonType.takeIf { it != REASON_TYPE_ERROR }?.let {
                    onReviewReportReasonListener(reasonType)
                    dismiss()
                } ?: run {
                    Snackbar.make(
                        binding.root, "신고 사유를 선택해주세요 ", Snackbar.LENGTH_SHORT
                    ).setBackgroundTint(
                            ContextCompat.getColor(
                                requireActivity(), R.color.text_secondary
                            )
                        ).show()
                }
            }
        }
    }

    private fun getCheckedResonType(binding: DialogReviewReportBinding): Int {
        val checkedButton = binding.radioGroupRRR.checkedRadioButtonId

        return when (checkedButton) {
            R.id.radioButtonRRRCommercial -> REASON_TYPE_COMMERCIAL
            R.id.radioButtonRRRCopyrightInfringement -> REASON_TYPE_COPYRIGHT_INFRINGEMENT
            R.id.radioButtonRRRObscenity -> REASON_TYPE_OBSCENITY
            R.id.radioButtonRRRVerbalAbuse -> REASON_TYPE_VERBAL_ABUSE
            R.id.radioButtonRRRPrivacyExposure -> REASON_TYPE_PRIVACY_EXPOSURE
            R.id.radioButtonRRROther -> REASON_TYPE_OTHER
            else -> REASON_TYPE_ERROR
        }
    }
}