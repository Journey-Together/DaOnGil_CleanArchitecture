package kr.techit.lion.presentation.myreview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.getstream.photoview.PhotoView
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.DialogPhotoBinding
import kr.techit.lion.presentation.myreview.adapter.PhotoVPAdapter

class PhotoDialog(
    private val imageList: List<String>,
    private val initialPosition: Int
) : DialogFragment(R.layout.dialog_photo) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogPhotoBinding.bind(view)


        settingToolbar(binding)
        settingViewPager(binding)
        settingIndicators(binding)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.decorView?.let { decorView ->
            val insetsController = WindowInsetsControllerCompat(dialog?.window!!, decorView)
            insetsController.isAppearanceLightStatusBars = false
        }
    }

    private fun settingToolbar(binding: DialogPhotoBinding) {
        binding.toolbarPhoto.setNavigationOnClickListener {
            dismiss()
        }
        binding.toolbarPhoto.setNavigationContentDescription(R.string.text_back_button)

        binding.textViewPhotoNum.text = getString(R.string.text_num_of_images, initialPosition + 1)
        binding.textViewPhotoTotalNum.text = getString(R.string.text_num_of_total_images, imageList.size)
    }

    private fun settingViewPager(binding: DialogPhotoBinding) {
        val photoVPAdapter = PhotoVPAdapter(imageList)
        binding.viewPagerPhoto.adapter = photoVPAdapter
        binding.viewPagerPhoto.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPagerPhoto.setCurrentItem(initialPosition, false)

        binding.viewPagerPhoto.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.textViewPhotoNum.text = getString(R.string.text_num_of_images, position + 1)

                resetZoom(binding, position - 1)
                resetZoom(binding, position + 1)
            }
        })
    }

    private fun settingIndicators(binding: DialogPhotoBinding) {
        binding.photoVPIndicator.setViewPager(binding.viewPagerPhoto)
    }

    private fun resetZoom(binding: DialogPhotoBinding, position: Int) {
        val viewHolder = (binding.viewPagerPhoto[0] as RecyclerView).findViewHolderForAdapterPosition(position)
        val photoView = viewHolder?.itemView?.findViewById<PhotoView>(R.id.photoView)
        photoView?.setScale(1.0f, true)
    }
}