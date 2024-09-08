package kr.tekit.lion.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.domain.model.OpenPlanInfo
import kr.tekit.lion.presentation.databinding.RowSchedulePublicBinding
import kr.tekit.lion.presentation.ext.setImageSmall

class SchedulePublicAdapter(
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<SchedulePublicAdapter.SchedulePublicViewHolder>() {

    private var items: MutableList<OpenPlanInfo> = mutableListOf()

    fun addItems(newItems: List<OpenPlanInfo>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    class SchedulePublicViewHolder(
        private val binding: RowSchedulePublicBinding, private val itemClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }

        fun bind(item: OpenPlanInfo) {
            with(binding) {
                textViewRowSchedulePublicPeriod.text = item.date
                textViewRowScheduleName.text = item.title
                textViewRowSchedulePublicNickname.text = item.memberNickname

                root.context.setImageSmall(imageViewRowSchedulePublicThumb, item.imageUrl)
                root.context.setImageSmall(imageViewRowSchedulePublicProfile, item.memberImageUrl)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulePublicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SchedulePublicViewHolder(
            RowSchedulePublicBinding.inflate(inflater, parent, false), itemClickListener
        )
    }

    override fun onBindViewHolder(holder: SchedulePublicViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}