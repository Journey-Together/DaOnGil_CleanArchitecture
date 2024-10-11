package kr.techit.lion.presentation.scheduleform.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.scheduleform.PlaceSearchInfoList
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemFormSearchResultBinding
import kr.techit.lion.presentation.databinding.ItemFormSearchTotalBinding
import kr.techit.lion.presentation.ext.setAccessibilityText
import kr.techit.lion.presentation.ext.setImageSmall

class FormSearchResultAdapter(
    private val onPlaceSelectedListener: (selectedPlacePosition: Int) -> Unit,
    private val onItemClickListener: (selectedPlacePosition: Int) -> Unit
) : ListAdapter<PlaceSearchInfoList, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_TOTAL_ELEMENTS -> {
                FormSearchTotalElementsViewHolder(
                    ItemFormSearchTotalBinding.inflate(inflater, parent, false)
                )
            }

            VIEW_TYPE_PLACE -> {
                FormSearchResultViewHolder(
                    ItemFormSearchResultBinding.inflate(inflater, parent, false),
                    onPlaceSelectedListener,
                    onItemClickListener
                )
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PlaceSearchInfoList.TotalElementsInfo -> VIEW_TYPE_TOTAL_ELEMENTS
        is PlaceSearchInfoList.PlaceSearchInfo -> VIEW_TYPE_PLACE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FormSearchTotalElementsViewHolder -> holder.bind(getItem(position) as PlaceSearchInfoList.TotalElementsInfo)
            is FormSearchResultViewHolder -> holder.bind(getItem(position) as PlaceSearchInfoList.PlaceSearchInfo)
        }
    }

    class FormSearchResultViewHolder(
        private val binding: ItemFormSearchResultBinding,
        private val onPlaceSelectedListener: (selectedPlacePosition: Int) -> Unit,
        private val onItemClickListener: (selectedPlacePosition: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonSearchResultAdd.setOnClickListener {
                // 장소 목록 앞에 검색 결과 수 item 이 추가되었으므로 AdapterPosition에서 1을 빼준다
                onPlaceSelectedListener(absoluteAdapterPosition - 1)
            }

            binding.root.setOnClickListener {
                onItemClickListener(absoluteAdapterPosition - 1)
            }
        }

        fun bind(placeSearchInfo: PlaceSearchInfoList.PlaceSearchInfo) {
            binding.apply {
                textSearchResultName.text = placeSearchInfo.placeName
                textSearchResultCategory.text = placeSearchInfo.category
                placeSearchInfo.imageUrl?.let {
                    imageSearchResultThumbnail.context.setImageSmall(imageSearchResultThumbnail, it)
                }

                textSearchResultName.setAccessibilityText(
                    itemView.context.getString(
                        R.string.accessibility_text_place,
                        placeSearchInfo.category,
                        placeSearchInfo.placeName
                    )
                )

                buttonSearchResultAdd.contentDescription = itemView.context.getString(
                    R.string.accessibility_text_schedule_add,
                    placeSearchInfo.category,
                    placeSearchInfo.placeName
                )

            }
        }
    }

    class FormSearchTotalElementsViewHolder(
        private val binding: ItemFormSearchTotalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(totalElements: PlaceSearchInfoList.TotalElementsInfo) {
            val numOfElements = totalElements.totalElements.toString()

            binding.textFormSearchTotalElements.apply {
                text = this.context.getString(R.string.text_number_of_result, numOfElements)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_TOTAL_ELEMENTS = 0
        private const val VIEW_TYPE_PLACE = 1

        val diffUtil = object : DiffUtil.ItemCallback<PlaceSearchInfoList>() {
            override fun areItemsTheSame(
                oldItem: PlaceSearchInfoList,
                newItem: PlaceSearchInfoList
            ): Boolean {
                return when {
                    oldItem is PlaceSearchInfoList.TotalElementsInfo && newItem is PlaceSearchInfoList.TotalElementsInfo -> {
                        oldItem.totalElements == newItem.totalElements
                    }

                    oldItem is PlaceSearchInfoList.PlaceSearchInfo && newItem is PlaceSearchInfoList.PlaceSearchInfo -> {
                        oldItem.placeId == newItem.placeId
                    }

                    else -> false
                }
            }

            override fun areContentsTheSame(
                oldItem: PlaceSearchInfoList,
                newItem: PlaceSearchInfoList
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}