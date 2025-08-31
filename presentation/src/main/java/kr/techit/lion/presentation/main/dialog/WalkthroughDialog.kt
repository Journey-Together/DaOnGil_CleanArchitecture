package kr.techit.lion.presentation.main.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.DialogWalkthroughBinding

class WalkthroughDialog : DialogFragment(R.layout.dialog_walkthrough) {
    private var pageCallback: ViewPager2.OnPageChangeCallback? = null
    private lateinit var pages: List<() -> Fragment>

    private companion object {
        const val REQ_SKIP = "skip"
        const val REQ_COMPLETE = "completeButtonClick"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogWalkthroughBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initPages()
        setupPager(binding)
        setupButtons(binding)
    }

    private fun initPages() {
        pages = listOf(
            { WalkthroughLayoutFragment.newInstance(R.layout.item_walkthrough_setting_theme) },
            { WalkthroughLayoutFragment.newInstance(R.layout.item_walkthrough_home) },
            { WalkthroughLayoutFragment.newInstance(R.layout.item_walkthrough_emergency) },
            { WalkthroughLayoutFragment.newInstance(R.layout.item_walkthrough_schedule) },
        )
    }

    private fun setupPager(binding: DialogWalkthroughBinding) {
        val vp = binding.walkthroughDialogVp
        binding.walkthroughDialogPageCount.text =
            getString(R.string.walkthrough_count_tv, 1, pages.size)

        vp.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = pages.size
            override fun createFragment(position: Int) = pages[position]()
        }

        pageCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.walkthroughDialogPageCount.text =
                    getString(R.string.walkthrough_count_tv, position + 1, pages.size)
                binding.walkthroughDialogPositiveBtn.text =
                    if (position == pages.lastIndex)
                        getString(R.string.walkthrough_dialog_finish_tv)
                    else
                        getString(R.string.walkthrough_dialog_next_tv)
            }
        }
        vp.registerOnPageChangeCallback(pageCallback!!)
    }

    private fun setupButtons(binding: DialogWalkthroughBinding) {
        val vp = binding.walkthroughDialogVp

        binding.walkthroughDialogPositiveBtn.setOnClickListener {
            val last = pages.lastIndex

            // 다음 페이지로 이동 혹은 종료
            if (vp.currentItem < last) {
                vp.setCurrentItem(vp.currentItem + 1, true)
            } else {
                setFragmentResult(REQ_COMPLETE, bundleOf())
                dismiss()
            }
        }

        binding.walkthroughDialogSkipTv.setOnClickListener {
            setFragmentResult(REQ_SKIP, bundleOf())
            dismiss()
        }
    }
}