package kr.techit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.scheduleform.DailySchedule
import kr.techit.lion.presentation.databinding.ItemFormConfirmBinding

class FormConfirmScheduleAdapter(private val dailyScheduleList: List<DailySchedule>) :
    RecyclerView.Adapter<FormConfirmScheduleAdapter.FormConfirmScheduleViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FormConfirmScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FormConfirmScheduleViewHolder(
            ItemFormConfirmBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FormConfirmScheduleViewHolder, position: Int) {
        holder.bind(dailyScheduleList[position])
    }

    override fun getItemCount(): Int = dailyScheduleList.size
    class FormConfirmScheduleViewHolder(
        private val binding: ItemFormConfirmBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dailySchedule: DailySchedule) {
            binding.textFormConfirmDate.text = dailySchedule.dailyDate

            val formConfirmPlaceAdapter = FormConfirmPlaceAdapter(
                dailySchedule.dailyPlaces
            )
            binding.recyclerViewFormConfirmPlaces.adapter = formConfirmPlaceAdapter

            binding.viewFormConfirmTopDeco.visibility = when (absoluteAdapterPosition) {
                0 -> View.INVISIBLE
                else -> View.VISIBLE
            }
        }
    }
}