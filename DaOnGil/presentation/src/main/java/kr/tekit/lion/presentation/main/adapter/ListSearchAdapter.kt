package kr.tekit.lion.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemListSearchAreaBinding
import kr.tekit.lion.presentation.databinding.ItemListSearchCategoryBinding
import kr.tekit.lion.presentation.databinding.ItemPlaceHighBinding
import kr.tekit.lion.presentation.main.adapter.multi_viewholder.ListSearchAreaViewHolder
import kr.tekit.lion.presentation.main.adapter.multi_viewholder.ListSearchCategoryViewHolder
import kr.tekit.lion.presentation.main.adapter.multi_viewholder.PlaceHighViewHolder
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.CategoryModel
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ListSearchUIModel
import kr.tekit.lion.presentation.main.model.PlaceModel

class ListSearchAdapter(
    private val uiScope: CoroutineScope,
    private val onClickPhysicalDisability: (DisabilityType.PhysicalDisability) -> Unit,
    private val onClickVisualImpairment: (DisabilityType.VisualImpairment) -> Unit,
    private val onClickHearingDisability: (DisabilityType.HearingImpairment) -> Unit,
    private val onClickInfantFamily: (DisabilityType.InfantFamily) -> Unit,
    private val onClickElderlyPeople: (DisabilityType.ElderlyPeople) -> Unit,
    private val onSelectArea: (String) -> Unit,
    private val onSelectSigungu: (String) -> Unit,
    private val onClickSearchButton: () -> Unit,
    private val onClickSortByLatestBtn: (String) -> Unit,
    private val onClickSortByPopularityBtn: (String) -> Unit,
    private val onClickSortByLetterBtn: (String) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val allDataList: MutableList<ListSearchUIModel> = mutableListOf()
    private val areaList: MutableList<String> = mutableListOf()
    private val sigunguList: MutableList<String> = mutableListOf()
    private val optionState: MutableMap<DisabilityType, Int> = HashMap()

    fun submitList(newData: List<ListSearchUIModel>) {
        val placeModels = allDataList.filterIsInstance<PlaceModel>()
        allDataList.removeAll(placeModels)
        allDataList.addAll(newData)
        notifyDataSetChanged()
    }

    fun submitAreaList(areas: List<String>) {
        areaList.addAll(areas)
    }

    fun submitSigunguList(list: List<String>) {
        sigunguList.clear()
        sigunguList.addAll(list)
        notifyDataSetChanged()
    }

    fun modifyOptionState(option: Map<DisabilityType, Int>) {
        optionState.clear()
        optionState.putAll(option)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (allDataList[position]) {
            is CategoryModel -> VIEW_TYPE_CATEGORY
            is PlaceModel -> VIEW_TYPE_PLACE
            is AreaModel -> VIEW_TYPE_AREA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)

        return when (viewType) {
            VIEW_TYPE_CATEGORY -> ListSearchCategoryViewHolder(
                ItemListSearchCategoryBinding.bind(v),
                uiScope,
                onClickPhysicalDisability,
                onClickVisualImpairment,
                onClickHearingDisability,
                onClickInfantFamily,
                onClickElderlyPeople
            )
            VIEW_TYPE_PLACE -> PlaceHighViewHolder(
                ItemPlaceHighBinding.bind(v)
            )
            VIEW_TYPE_AREA -> ListSearchAreaViewHolder(
                uiScope,
                ItemListSearchAreaBinding.bind(v),
                onSelectArea,
                onSelectSigungu,
                onClickSearchButton,
                onClickSortByLatestBtn,
                onClickSortByPopularityBtn,
                onClickSortByLetterBtn
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = allDataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = allDataList[position]
        when (holder) {
            is ListSearchCategoryViewHolder -> holder.bind(optionState)
            is ListSearchAreaViewHolder -> holder.bind(areaList, sigunguList)
            is PlaceHighViewHolder -> holder.bind(item as PlaceModel)
        }
    }

    companion object {
        val VIEW_TYPE_CATEGORY = R.layout.item_list_search_category
        val VIEW_TYPE_PLACE = R.layout.item_place_high
        val VIEW_TYPE_AREA = R.layout.item_list_search_area
    }
}
