package kr.techit.lion.presentation.schedule.customview

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.BottomSheetScheduleManageBinding
import kr.techit.lion.presentation.main.dialog.ConfirmDialog

class ScheduleManageBottomSheet(
    private val isPublic: Boolean,
    private val isReport: Boolean?,
    private val onScheduleStateToggleListener: () -> Unit,
    private val onScheduleDeleteClickListener: () -> Unit,
    private val onScheduleEditClickListener: () -> Unit
) : BottomSheetDialogFragment(R.layout.bottom_sheet_schedule_manage) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetScheduleManageBinding.bind(view)

        initView(binding, isPublic)
    }

    private fun initView(binding: BottomSheetScheduleManageBinding, isPublic: Boolean) {
        with(binding){
            when (isPublic) {
                true -> {
                    iconScheduleManagePublicToggle.setImageResource(R.drawable.icon_lock)
                    textViewScheduleManagePublicToggle.text =
                        getString(R.string.text_schedule_make_private)
                }

                false -> {
                    iconScheduleManagePublicToggle.setImageResource(R.drawable.icon_lock_open)
                    textViewScheduleManagePublicToggle.text =
                        getString(R.string.text_schedule_make_public)
                }
            }

            if(isReport == true){
                layoutScheduleManagePublicToggle.visibility = View.GONE
            }

            layoutScheduleManageEdit.setOnClickListener {
                onScheduleEditClickListener()
            }
            layoutScheduleManageDelete.setOnClickListener {
                showScheduleDeleteDialog()
            }

            layoutScheduleManagePublicToggle.setOnClickListener {
                onScheduleStateToggleListener()
                dismiss()
            }
        }
    }

    private fun showScheduleDeleteDialog() {
        val dialog = ConfirmDialog(
            "여행 일정 삭제",
            "삭제한 일정은 되돌릴 수 없습니다.",
            "삭제하기"
        ) {
            onScheduleDeleteClickListener()
            dismiss()
        }
        dialog.isCancelable = false
        dialog.show(requireActivity().supportFragmentManager, "ScheduleManageDialog")
    }

    override fun getTheme(): Int {
        return R.style.category_bottom_sheet_dialog_theme
    }

}