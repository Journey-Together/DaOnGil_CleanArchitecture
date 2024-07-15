package kr.tekit.lion.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemListSearchAreaBinding
import kr.tekit.lion.presentation.databinding.ItemListSearchCategoryBinding
import kr.tekit.lion.presentation.databinding.ItemNoPlaceBinding
import kr.tekit.lion.presentation.databinding.ItemPlaceHighBinding
import kr.tekit.lion.presentation.main.adapter.multi_viewholder.ListSearchAreaViewHolder
import kr.tekit.lion.presentation.main.adapter.multi_viewholder.ListSearchCategoryViewHolder
import kr.tekit.lion.presentation.main.adapter.multi_viewholder.NoPlaceViewHolder
import kr.tekit.lion.presentation.main.adapter.multi_viewholder.PlaceHighViewHolder
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.CategoryModel
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.ListSearchUIModel
import kr.tekit.lion.presentation.main.model.NoPlaceModel
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.PlaceModel
import kr.tekit.lion.presentation.main.model.VisualImpairment

class ListSearchAdapter(
    private val uiScope: CoroutineScope,
    private val onClickPhysicalDisability: (PhysicalDisability) -> Unit,
    private val onClickVisualImpairment: (VisualImpairment) -> Unit,
    private val onClickHearingDisability: (HearingImpairment) -> Unit,
    private val onClickInfantFamily: (InfantFamily) -> Unit,
    private val onClickElderlyPeople: (ElderlyPeople) -> Unit,
    private val onSelectArea: (String) -> Unit,
    private val onSelectSigungu: (String) -> Unit,
    private val onClickSortByLatestBtn: (String) -> Unit,
    private val onClickSortByPopularityBtn: (String) -> Unit,
    private val onClickSortByLetterBtn: (String) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val allDataList: MutableList<ListSearchUIModel> = mutableListOf()
    private val areaList: MutableList<String> = mutableListOf()
    private val sigunguList: MutableList<String> = mutableListOf()
    private val optionState: MutableMap<DisabilityType, Int> = HashMap()

    fun submitList(newData: List<ListSearchUIModel>) {
        val oldPlaceModels = allDataList.filterIsInstance<PlaceModel>()
        if (oldPlaceModels.isNotEmpty()) {
            allDataList.removeAll(oldPlaceModels)
        }

        val oldNoPlaceModels = allDataList.filterIsInstance<NoPlaceModel>()
        val newPlaceModels = newData.filterIsInstance<PlaceModel>()

        allDataList.addAll(newData)

        if (newPlaceModels.isNotEmpty()) {
            allDataList.removeAll(oldNoPlaceModels)
        } else {
            allDataList.removeAll(allDataList.filterIsInstance<NoPlaceModel>())
            allDataList.add(NoPlaceModel())
        }

        notifyDataSetChanged()
    }

    fun submitErrorMessage(errorMessage: String) {
        allDataList.clear()
        allDataList.add(NoPlaceModel(errorMessage))
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
            is CategoryModel -> CategoryModel.id
            is PlaceModel -> PlaceModel().id
            is AreaModel -> AreaModel.id
            is NoPlaceModel -> NoPlaceModel().id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(viewType, parent, false)

        return when (viewType) {
            CategoryModel.id-> ListSearchCategoryViewHolder(
                ItemListSearchCategoryBinding.bind(v),
                uiScope,
                onClickPhysicalDisability,
                onClickVisualImpairment,
                onClickHearingDisability,
                onClickInfantFamily,
                onClickElderlyPeople
            )

            PlaceModel().id -> PlaceHighViewHolder(
                ItemPlaceHighBinding.bind(v)
            )

            AreaModel.id -> ListSearchAreaViewHolder(
                uiScope,
                ItemListSearchAreaBinding.bind(v),
                onSelectArea,
                onSelectSigungu,
                onClickSortByLatestBtn,
                onClickSortByPopularityBtn,
                onClickSortByLetterBtn
            )

            NoPlaceModel().id -> NoPlaceViewHolder(
                ItemNoPlaceBinding.bind(v)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = allDataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = allDataList[position]
        val place = allDataList.filterIsInstance<PlaceModel>()
        val msg = allDataList.filterIsInstance<NoPlaceModel>()
            .firstOrNull()?.msg ?: NoPlaceModel().msg

        when (holder) {
            is ListSearchCategoryViewHolder -> holder.bind(optionState)
            is ListSearchAreaViewHolder -> holder.bind(
                if (place.isEmpty()) 0 else place[0].itemCount,
                areaList,
                sigunguList
            )
            is PlaceHighViewHolder -> holder.bind(item as PlaceModel)
            is NoPlaceViewHolder -> holder.bind(msg)
        }
    }
}
