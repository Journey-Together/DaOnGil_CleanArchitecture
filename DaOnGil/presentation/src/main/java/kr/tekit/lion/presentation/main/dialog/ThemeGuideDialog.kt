package kr.tekit.lion.presentation.main.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.DialogModeSettingBinding
import kr.tekit.lion.presentation.databinding.DialogThemeGuideBinding

class ThemeGuideDialog : DialogFragment(R.layout.dialog_theme_guide) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogThemeGuideBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.dialogModePositiveBtn.setOnClickListener {
            dismiss()
            setFragmentResult("completeButtonClick", bundleOf())
        }
    }
}
