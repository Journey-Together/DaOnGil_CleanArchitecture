package kr.techit.lion.presentation.main.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.DialogModeSettingBinding

class ThemeSettingDialog : DialogFragment(R.layout.dialog_mode_setting) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogModeSettingBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialogModeNegativeBtn.setOnClickListener {
            setFragmentResult("negativeButtonClick", bundleOf())
            dismiss()
        }

        binding.dialogModePositiveBtn.setOnClickListener {
            setFragmentResult("positiveButtonClick", bundleOf())
            dismiss()
        }
    }
}
