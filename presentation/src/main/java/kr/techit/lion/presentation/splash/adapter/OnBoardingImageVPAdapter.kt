package kr.techit.lion.presentation.splash.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.onboarding.OnBoardingPage
import kr.techit.lion.presentation.splash.adapter.OnBoardingViewHolder.Companion.OnBoardingViewHolder

class OnBoardingImageVPAdapter(
    private val pages: List<OnBoardingPage>,
) : RecyclerView.Adapter<OnBoardingViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): OnBoardingViewHolder = OnBoardingViewHolder(parent)

    override fun getItemCount(): Int = pages.size

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }
}
