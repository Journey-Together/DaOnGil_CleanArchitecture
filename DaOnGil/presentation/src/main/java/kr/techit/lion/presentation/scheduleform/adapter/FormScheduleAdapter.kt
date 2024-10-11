package kr.techit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.scheduleform.DailySchedule
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemFormScheduleBinding
import kr.techit.lion.presentation.ext.setAccessibilityText

class FormScheduleAdapter(
    private val dailyScheduleList: List<DailySchedule>,
    private val onAddButtonClickListener: (schedulePosition: Int) -> Unit,
    private val onItemClickListener: (schedulePosition: Int, placePosition: Int) -> Unit,
    private val onRemoveButtonClickListener: (schedulePosition: Int, placePosition: Int) -> Unit
) : RecyclerView.Adapter<FormScheduleAdapter.FormScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FormScheduleViewHolder(
            ItemFormScheduleBinding.inflate(inflater, parent, false),
            onAddButtonClickListener,
            onItemClickListener,
            onRemoveButtonClickListener
        )
    }

    override fun onBindViewHolder(holder: FormScheduleViewHolder, position: Int) {
        holder.bind(dailyScheduleList[position])
    }

    override fun getItemCount(): Int = dailyScheduleList.size

    class FormScheduleViewHolder(
        private val binding: ItemFormScheduleBinding,
        private val onAddButtonClickListener: (schedulePosition: Int) -> Unit,
        private val onItemClickListener: (schedulePosition: Int, placePosition: Int) -> Unit,
        private val onRemoveButtonClickListener: (schedulePosition: Int, placePosition: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 여행지 추가 버튼
            binding.buttonFormAddPlace.setOnClickListener {
                onAddButtonClickListener(absoluteAdapterPosition)
            }
        }

        fun bind(dailySchedule: DailySchedule) {
            binding.textFormScheduleDate.text = dailySchedule.dailyDate

            // 추가할 여행지목록 Adapter
            val formPlaceAdapter = FormPlaceAdapter(
                dailySchedule.dailyPlaces,
                absoluteAdapterPosition,
                onItemClickListener,
                onRemoveButtonClickListener
            )
            binding.recyclerViewFormPlaces.adapter = formPlaceAdapter

            binding.buttonFormAddPlace.setAccessibilityText(
                itemView.context.getString(
                    R.string.accessibility_text_schedule_form_add_place,
                    dailySchedule.dailyDate
                )
            )

            binding.viewFormTopDeco.visibility = when (absoluteAdapterPosition) {
                0 -> View.INVISIBLE
                else -> View.VISIBLE
            }
        }
    }
}