package kr.techit.lion.presentation.schedule.customview

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.BottomSheetScheduleReviewManageBinding
import kr.techit.lion.presentation.main.dialog.ConfirmDialog

class ScheduleReviewManageBottomSheet(
    private val onReviewDeleteClickListener: () -> Unit,
    private val onReviewEditClickListener: () -> Unit,
) : BottomSheetDialogFragment(R.layout.bottom_sheet_schedule_review_manage) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetScheduleReviewManageBinding.bind(view)

        initView(binding)
    }

    private fun initView(binding: BottomSheetScheduleReviewManageBinding) {
        binding.apply {
            textViewScheduleReviewManageEdit.setOnClickListener {
                onReviewEditClickListener()
            }

            textViewScheduleReviewManageDelete.setOnClickListener {
                // 리뷰 삭제 다이얼로그
                showScheduleReviewDeleteDialog()
            }
        }
    }

    private fun showScheduleReviewDeleteDialog() {
        val dialog = ConfirmDialog(
            "여행 후기 삭제", "삭제한 후기는 되돌릴 수 없습니다.", "삭제하기"
        ) {
            onReviewDeleteClickListener()
            dismiss()
        }
        dialog.isCancelable = false
        dialog.show(requireActivity().supportFragmentManager, "ScheduleReviewManageDialog")
    }

    override fun getTheme(): Int {
        return R.style.category_bottom_sheet_dialog_theme
    }
}