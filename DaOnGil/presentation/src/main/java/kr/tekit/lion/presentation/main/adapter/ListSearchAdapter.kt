package kr.tekit.lion.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemListSearchAreaBinding
import kr.tekit.lion.presentation.databinding.ItemListSearchCategoryBinding
import kr.tekit.lion.presentation.databinding.ItemListSearchSigunguBinding
import kr.tekit.lion.presentation.databinding.ItemListSearchSortBinding
import kr.tekit.lion.presentation.databinding.ItemNoPlaceBinding
import kr.tekit.lion.presentation.databinding.ItemPlaceHighBinding
import kr.tekit.lion.presentation.main.adapter.viewholder.AreaViewHolder
import kr.tekit.lion.presentation.main.adapter.viewholder.CategoryViewHolder
import kr.tekit.lion.presentation.main.adapter.viewholder.NoPlaceViewHolder
import kr.tekit.lion.presentation.main.adapter.viewholder.PlaceHighViewHolder
import kr.tekit.lion.presentation.main.adapter.viewholder.SigunguViewHolder
import kr.tekit.lion.presentation.main.adapter.viewholder.ItemCountViewHolder
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.CategoryModel
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.ListSearchUIModel
import kr.tekit.lion.presentation.main.model.NoPlaceModel
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.PlaceModel
import kr.tekit.lion.presentation.main.model.SigunguModel
import kr.tekit.lion.presentation.main.model.SortModel
import kr.tekit.lion.presentation.main.model.VisualImpairment

class ListSearchAdapter(
    private val uiScope: CoroutineScope,
    private val onClickPhysicalDisability: (PhysicalDisability) -> Unit,
    private val onClickVisualImpairment: (VisualImpairment) -> Unit,
    private val onClickHearingDisability: (HearingImpairment) -> Unit,
    private val onClickInfantFamily: (InfantFamily) -> Unit,
    private val onClickElderlyPeople: (ElderlyPeople) -> Unit,
    private val onSelectArea: (String) -> Unit,
    private val onSelectSigungu: (String) -> Unit
) : ListAdapter<ListSearchUIModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CategoryModel -> R.layout.item_list_search_category
            is PlaceModel -> R.layout.item_place_high
            is AreaModel -> R.layout.item_list_search_area
            is SigunguModel -> R.layout.item_list_search_sigungu
            is SortModel -> R.layout.item_list_search_sort
            is NoPlaceModel -> R.layout.item_no_place
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)

        return when (viewType) {
            R.layout.item_list_search_category -> CategoryViewHolder(
                ItemListSearchCategoryBinding.bind(v),
                uiScope,
                onClickPhysicalDisability,
                onClickVisualImpairment,
                onClickHearingDisability,
                onClickInfantFamily,
                onClickElderlyPeople
            )
            R.layout.item_place_high -> PlaceHighViewHolder(
                ItemPlaceHighBinding.bind(v)
            )
            R.layout.item_list_search_area -> AreaViewHolder(
                ItemListSearchAreaBinding.bind(v),
                onSelectArea,
            )
            R.layout.item_list_search_sort -> ItemCountViewHolder(
                ItemListSearchSortBinding.bind(v)
            )
            R.layout.item_list_search_sigungu -> SigunguViewHolder(
                ItemListSearchSigunguBinding.bind(v),
                onSelectSigungu
            )
            R.layout.item_no_place -> NoPlaceViewHolder(ItemNoPlaceBinding.bind(v))
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
    }
}
