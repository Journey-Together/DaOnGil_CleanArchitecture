package kr.techit.lion.presentation.splash.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.databinding.ItemOnboardingVpBinding
import kr.techit.lion.presentation.onboarding.OnBoardingPage

class OnBoardingViewHolder private constructor(private val binding: ItemOnboardingVpBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(page: OnBoardingPage) {
        binding.itemOnboardingIv.setImageDrawable(page.image)
        binding.itemOnboardingTv1.text = page.title
        binding.itemOnboardingTv2.text = page.description
        binding.itemOnboardingTv3.text = page.extra
    }

    companion object {
        fun OnBoardingViewHolder(parent: ViewGroup): OnBoardingViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemOnboardingVpBinding.inflate(inflater, parent, false)
            return OnBoardingViewHolder(binding)
        }
    }
}
