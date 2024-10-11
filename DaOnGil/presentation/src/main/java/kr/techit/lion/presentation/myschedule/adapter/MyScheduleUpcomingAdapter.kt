package kr.techit.lion.presentation.myschedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.techit.lion.domain.model.schedule.MyUpcomingScheduleInfo
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemMyScheduleUpcomingBinding
import kr.techit.lion.presentation.ext.convertPeriodToDate
import kr.techit.lion.presentation.ext.convertStringToDate
import kr.techit.lion.presentation.ext.setAccessibilityText

class MyScheduleUpcomingAdapter(
    private val onScheduleItemClicked: (layoutPosition: Int) -> Unit
) : ListAdapter<MyUpcomingScheduleInfo, MyScheduleUpcomingAdapter.UpcomingScheduleViewHolder>(
    diffUtil
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UpcomingScheduleViewHolder(
            ItemMyScheduleUpcomingBinding.inflate(
                inflater,
                parent,
                false
            ), onScheduleItemClicked
        )
    }

    override fun onBindViewHolder(holder: UpcomingScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UpcomingScheduleViewHolder(
        private val binding: ItemMyScheduleUpcomingBinding,
        private val onScheduleItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onScheduleItemClicked(absoluteAdapterPosition)
            }
        }

        fun bind(mySchedule: MyUpcomingScheduleInfo) {
            binding.apply {
                textViewMyScheduleUpcomingName.text = mySchedule.title
                textViewMyScheduleUpcomingDDay.text = mySchedule.remainDate
                textViewMyScheduleUpcomingPeriod.text = itemView.context.getString(
                    R.string.text_schedule_period,
                    mySchedule.startDate,
                    mySchedule.endDate
                )
                if (mySchedule.imageUrl != "") {
                    Glide.with(itemView.context)
                        .load(mySchedule.imageUrl)
                        .placeholder(R.drawable.empty_view_small)
                        .error(R.drawable.empty_view_small)
                        .override(50, 50)
                        .into(binding.imageViewMyScheduleUpcoming)
                }


                // 남은 기간을 "n"일 전 이라고 읽어주도록 설정
                val remainDateNumber = mySchedule.remainDate.filter { it.isDigit() }
                textViewMyScheduleUpcomingDDay.setAccessibilityText(
                    itemView.context.getString(
                        R.string.text_schedule_remain_date,
                        remainDateNumber
                    )
                )

                // 일정 기간을 년,월,일로 읽어주도록 설정
                val startDate = mySchedule.startDate.convertStringToDate()
                val endDate = mySchedule.endDate.convertStringToDate()

                val schedulePeriod = startDate.convertPeriodToDate(endDate)
                textViewMyScheduleUpcomingPeriod.setAccessibilityText(
                    schedulePeriod
                )
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MyUpcomingScheduleInfo>() {
            override fun areItemsTheSame(
                oldItem: MyUpcomingScheduleInfo,
                newItem: MyUpcomingScheduleInfo
            ): Boolean {
                return oldItem.planId == newItem.planId
            }

            override fun areContentsTheSame(
                oldItem: MyUpcomingScheduleInfo,
                newItem: MyUpcomingScheduleInfo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}