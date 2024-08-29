package kr.tekit.lion.presentation.bookmark.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.tekit.lion.domain.model.PlanBookmark
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemPlanBookmarkBinding

class PlanBookmarkRVAdapter(
    private val planBookmarkList: List<PlanBookmark>,
    private val itemClickListener: (Int) -> Unit,
    private val onBookmarkClick: (Long) -> Unit
) : RecyclerView.Adapter<PlanBookmarkRVAdapter.PlanBookmarkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanBookmarkViewHolder {
        val binding = ItemPlanBookmarkBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return PlanBookmarkViewHolder(binding, itemClickListener, onBookmarkClick)
    }

    override fun getItemCount(): Int = planBookmarkList.size

    override fun onBindViewHolder(holder: PlanBookmarkViewHolder, position: Int) {
        holder.bind(planBookmarkList[position])
    }

    class PlanBookmarkViewHolder(
        val binding: ItemPlanBookmarkBinding,
        private val itemClickListener: (Int) -> Unit,
        private val onBookmarkClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener.invoke(bindingAdapterPosition)
            }
        }

        fun bind(planBookmark: PlanBookmark) {
            binding.textViewScheduleBookmarkTitle.text = planBookmark.title
            binding.textViewScheduleBookmarkNickname.text = planBookmark.name

            binding.scheduleBookmarkBtn.setOnClickListener {
                onBookmarkClick(planBookmark.planId)
            }

            Glide.with(binding.imageViewScheduleBookmark.context)
                .load(planBookmark.image)
                .placeholder(R.drawable.empty_view_long)
                .error(R.drawable.empty_view_long)
                .into(binding.imageViewScheduleBookmark)

            Glide.with(binding.imageViewBookmarkUserProfile.context)
                .load(planBookmark.profileImg)
                .placeholder(R.drawable.empty_view)
                .error(R.drawable.empty_view)
                .into(binding.imageViewBookmarkUserProfile)
        }
    }
}