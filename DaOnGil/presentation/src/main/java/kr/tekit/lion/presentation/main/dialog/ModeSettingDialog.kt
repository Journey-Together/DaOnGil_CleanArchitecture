package kr.tekit.lion.presentation.main.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.DialogModeSettingBinding

class ModeSettingDialog : DialogFragment(R.layout.dialog_mode_setting) {

    private lateinit var onNegativeButtonClick: () -> Unit
    private lateinit var onPositiveButtonClick: () -> Unit

    companion object {
        fun newInstance(
            onNegativeClick: () -> Unit,
            onPositiveClick: () -> Unit
        ): ModeSettingDialog {
            val dialog = ModeSettingDialog()
            dialog.onNegativeButtonClick = onNegativeClick
            dialog.onPositiveButtonClick = onPositiveClick
            return dialog
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogModeSettingBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialogModeNegativeBtn.setOnClickListener {
            dismiss()
            onNegativeButtonClick()
        }

        binding.dialogModePositiveBtn.setOnClickListener {
            dismiss()
            onPositiveButtonClick()
        }
    }
}
