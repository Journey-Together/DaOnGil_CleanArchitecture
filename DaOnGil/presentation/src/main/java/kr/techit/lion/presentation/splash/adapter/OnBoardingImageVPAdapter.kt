package kr.techit.lion.presentation.splash.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.databinding.ItemOnboardingVpBinding
import kr.techit.lion.presentation.login.model.OnBoardingPage

class OnBoardingImageVPAdapter(
    private val pages: List<OnBoardingPage>,
) :  RecyclerView.Adapter<OnBoardingImageVPAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding : ItemOnboardingVpBinding = ItemOnboardingVpBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = pages.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    class ImageViewHolder(private val binding: ItemOnboardingVpBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(page: OnBoardingPage) {
            binding.itemOnboardingIv.setImageDrawable(page.imageId)
            binding.itemOnboardingTv1.text = page.text1
            binding.itemOnboardingTv2.text = page.text2
            binding.itemOnboardingTv3.text = page.text3
        }
    }
}