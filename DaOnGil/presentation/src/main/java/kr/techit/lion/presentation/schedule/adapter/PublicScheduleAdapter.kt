package kr.techit.lion.presentation.schedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.OpenPlanInfo
import kr.techit.lion.presentation.databinding.ItemPublicScheduleBinding
import kr.techit.lion.presentation.ext.setImage
import kr.techit.lion.presentation.ext.setImageSmall

class PublicScheduleAdapter(
    private val onPublicScheduleClicked: (Int) -> Unit
) : ListAdapter<OpenPlanInfo, PublicScheduleAdapter.PublicScheduleViewHolder>(diffUtil) {

    companion object diffUtil : DiffUtil.ItemCallback<OpenPlanInfo>() {
        override fun areItemsTheSame(oldItem: OpenPlanInfo, newItem: OpenPlanInfo): Boolean {
            return oldItem.planId == newItem.planId
        }

        override fun areContentsTheSame(oldItem: OpenPlanInfo, newItem: OpenPlanInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PublicScheduleViewHolder(
            ItemPublicScheduleBinding.inflate(inflater, parent, false),
            onPublicScheduleClicked
        )
    }

    override fun onBindViewHolder(holder: PublicScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PublicScheduleViewHolder(
        private val binding: ItemPublicScheduleBinding,
        private val onPublicScheduleClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onPublicScheduleClicked(absoluteAdapterPosition)
            }
        }

        fun bind(publicSchedule: OpenPlanInfo) {
            binding.apply {
                textViewItemPublicScheduleName.text = publicSchedule.title
                textViewItemPublicScheduleNickname.text = publicSchedule.memberNickname
                textViewItemPublicSchedulePeriod.text = publicSchedule.date

                // 여행지 대표 이미지
                itemView.context.setImage(imageViewItemPublicScheduleThumb, publicSchedule.imageUrl)

                // 프로필 사진
                itemView.context.setImageSmall(imageViewItemPublicScheduleProfile, publicSchedule.memberImageUrl)

            }
        }
    }
}