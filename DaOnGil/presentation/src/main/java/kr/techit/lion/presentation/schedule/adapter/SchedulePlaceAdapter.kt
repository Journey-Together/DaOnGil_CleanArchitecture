package kr.techit.lion.presentation.schedule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.SchedulePlace
import kr.techit.lion.presentation.databinding.ItemScheduleEmptyBinding
import kr.techit.lion.presentation.databinding.ItemSchedulePlaceBinding

class SchedulePlaceAdapter(
    private val schedulePlaces: List<SchedulePlace?>, private val placeClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_PLACE = 0
    private val VIEW_TYPE_EMPTY = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            VIEW_TYPE_PLACE -> {
                return SchedulePlaceViewHolder(
                    ItemSchedulePlaceBinding.inflate(
                        inflater, parent, false
                    ), placeClickListener
                )
            }
            // VIEW_TYPE_EMPTY
            else -> {
                return ScheduleEmptyViewHolder(
                    ItemScheduleEmptyBinding.inflate(
                        inflater, parent, false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SchedulePlaceViewHolder -> {
                schedulePlaces[position]?.let {
                    holder.bind(it)
                }
            }

            is ScheduleEmptyViewHolder -> {}
        }
    }


    override fun getItemCount(): Int {
        return if (schedulePlaces.isEmpty()) 1 else schedulePlaces.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (schedulePlaces.isNotEmpty()) VIEW_TYPE_PLACE else VIEW_TYPE_EMPTY
    }
}

class SchedulePlaceViewHolder(
    private val binding: ItemSchedulePlaceBinding, private val placeClickListener: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            placeClickListener.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(
        schedulePlace: SchedulePlace
    ) {
        with(binding) {
            schedulePlacePostion.text = (absoluteAdapterPosition + 1).toString()
            textViewItemSPlaceName.text = schedulePlace.name
            textViewItemSPlaceCategory.text = schedulePlace.category

            // 무장애 유형 정보
            // 1 (지체 장애), 2 (시각 장애), 3 (청각 장애), 4 (유아 동반), 5 (고령자)
            schedulePlace.disability.forEach {
                when (it) {
                    1 -> iconISPPhysicalDisability.visibility = View.VISIBLE
                    2 -> iconISPVisualImpairment.visibility = View.VISIBLE
                    3 -> iconISPHearingImpairment.visibility = View.VISIBLE
                    4 -> iconISPInfantFamily.visibility = View.VISIBLE
                    5 -> iconISPElderlyPeople.visibility = View.VISIBLE
                }
            }
        }
    }
}

class ScheduleEmptyViewHolder(private val binding: ItemScheduleEmptyBinding) :
    RecyclerView.ViewHolder(binding.root)