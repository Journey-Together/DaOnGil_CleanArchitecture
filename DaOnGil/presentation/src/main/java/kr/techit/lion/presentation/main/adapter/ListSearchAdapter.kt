package kr.techit.lion.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemListSearchAreaBinding
import kr.techit.lion.presentation.databinding.ItemListSearchCategoryBinding
import kr.techit.lion.presentation.databinding.ItemListSearchSigunguBinding
import kr.techit.lion.presentation.databinding.ItemListSearchSortBinding
import kr.techit.lion.presentation.databinding.ItemNoPlaceBinding
import kr.techit.lion.presentation.databinding.ItemPlaceHighBinding
import kr.techit.lion.presentation.main.adapter.viewholder.AreaViewHolder
import kr.techit.lion.presentation.main.adapter.viewholder.CategoryViewHolder
import kr.techit.lion.presentation.main.adapter.viewholder.NoPlaceViewHolder
import kr.techit.lion.presentation.main.adapter.viewholder.PlaceHighViewHolder
import kr.techit.lion.presentation.main.adapter.viewholder.SigunguViewHolder
import kr.techit.lion.presentation.main.adapter.viewholder.ItemCountViewHolder
import kr.techit.lion.presentation.main.model.AreaModel
import kr.techit.lion.presentation.main.model.CategoryModel
import kr.techit.lion.presentation.main.model.ElderlyPeople
import kr.techit.lion.presentation.main.model.HearingImpairment
import kr.techit.lion.presentation.main.model.InfantFamily
import kr.techit.lion.presentation.main.model.ListSearchUIModel
import kr.techit.lion.presentation.main.model.NoPlaceModel
import kr.techit.lion.presentation.main.model.PhysicalDisability
import kr.techit.lion.presentation.main.model.PlaceModel
import kr.techit.lion.presentation.main.model.SigunguModel
import kr.techit.lion.presentation.main.model.SortModel
import kr.techit.lion.presentation.main.model.VisualImpairment

class ListSearchAdapter(
    private val uiScope: CoroutineScope,
    private val onClickPhysicalDisability: (PhysicalDisability) -> Unit,
    private val onClickVisualImpairment: (VisualImpairment) -> Unit,
    private val onClickHearingDisability: (HearingImpairment) -> Unit,
    private val onClickInfantFamily: (InfantFamily) -> Unit,
    private val onClickElderlyPeople: (ElderlyPeople) -> Unit,
    private val onSelectArea: (String) -> Unit,
    private val onSelectSigungu: (String) -> Unit,
    private val onSelectPlace: (Long) -> Unit
) : ListAdapter<ListSearchUIModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CategoryModel -> VIEW_TYPE_CATEGORY
            is PlaceModel -> VIEW_TYPE_PLACE
            is AreaModel -> VIEW_TYPE_AREA
            is SigunguModel -> VIEW_TYPE_SIGUNGU
            is SortModel -> VIEW_TYPE_SORT
            is NoPlaceModel -> VIEW_TYPE_NO_PLACE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)

        return when (viewType) {
            VIEW_TYPE_CATEGORY -> CategoryViewHolder(
                ItemListSearchCategoryBinding.bind(v),
                uiScope,
                onClickPhysicalDisability,
                onClickVisualImpairment,
                onClickHearingDisability,
                onClickInfantFamily,
                onClickElderlyPeople
            )
            VIEW_TYPE_PLACE -> PlaceHighViewHolder(
                ItemPlaceHighBinding.bind(v),
                onSelectPlace
            )
            VIEW_TYPE_AREA -> AreaViewHolder(
                ItemListSearchAreaBinding.bind(v),
                onSelectArea,
            )
            VIEW_TYPE_SORT -> ItemCountViewHolder(
                ItemListSearchSortBinding.bind(v)
            )
            VIEW_TYPE_SIGUNGU -> SigunguViewHolder(
                ItemListSearchSigunguBinding.bind(v),
                onSelectSigungu
            )
            VIEW_TYPE_NO_PLACE -> NoPlaceViewHolder(ItemNoPlaceBinding.bind(v))
            else -> throw IllegalArgumentException("Unknown View Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CategoryModel -> {
                (holder as CategoryViewHolder).bind(item)
            }
            is AreaModel -> {
                (holder as AreaViewHolder).bind(item)
            }
            is SigunguModel -> {
                (holder as SigunguViewHolder).bind(item)
            }
            is PlaceModel -> {
                (holder as PlaceHighViewHolder).bind(item)
            }
            is SortModel -> {
                (holder as ItemCountViewHolder).bind(item)
            }
            is NoPlaceModel -> {
                (holder as NoPlaceViewHolder).bind(item.msg)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListSearchUIModel>() {
            override fun areItemsTheSame(oldItem: ListSearchUIModel, newItem: ListSearchUIModel): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(oldItem: ListSearchUIModel, newItem: ListSearchUIModel): Boolean {
                return oldItem == newItem
            }
        }

        private val VIEW_TYPE_CATEGORY = R.layout.item_list_search_category
        private val VIEW_TYPE_AREA = R.layout.item_list_search_area
        private val VIEW_TYPE_SIGUNGU = R.layout.item_list_search_sigungu
        private val VIEW_TYPE_SORT = R.layout.item_list_search_sort
        private val VIEW_TYPE_NO_PLACE = R.layout.item_no_place
        val VIEW_TYPE_PLACE = R.layout.item_place_high
    }
}
