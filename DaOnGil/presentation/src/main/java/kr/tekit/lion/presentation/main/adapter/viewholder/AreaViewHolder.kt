package kr.tekit.lion.presentation.main.adapter.viewholder

import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemListSearchAreaBinding
import kr.tekit.lion.presentation.ext.setClickEvent
import kr.tekit.lion.presentation.main.model.AreaModel
import kr.tekit.lion.presentation.main.model.SortByLatest
import kr.tekit.lion.presentation.main.model.SortByLetter
import kr.tekit.lion.presentation.main.model.SortByPopularity

class ListSearchAreaViewHolder(
    private val uiScope: CoroutineScope,
    private val binding: ItemListSearchAreaBinding,
    private val onSelectArea: (String) -> Unit,
    private val onSelectSigungu: (String) -> Unit,
    private val onClickSortByLatestBtn: (String) -> Unit,
    private val onClickSortByPopularityBtn: (String) -> Unit,
    private val onClickSortByLetterBtn: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        with(binding) {
            val defaultColor = root.context.getColor(R.color.select_area_text)
            val selectedColor = root.context.getColor(R.color.search_view_main)

            btnLatest.setTextColor(selectedColor)

            btnPopularity.setClickEvent(uiScope) {
                onClickSortByPopularityBtn(SortByPopularity.sortCode)
                btnPopularity.setTextColor(selectedColor)
                btnLatest.setTextColor(defaultColor)
                btnLetter.setTextColor(defaultColor)
            }

            btnLatest.setClickEvent(uiScope) {
                onClickSortByLatestBtn(SortByLatest.sortCode)
                btnLatest.setTextColor(selectedColor)
                btnLetter.setTextColor(defaultColor)
                btnPopularity.setTextColor(defaultColor)
            }

            btnLetter.setClickEvent(uiScope) {
                onClickSortByLetterBtn(SortByLetter.sortCode)
                btnLetter.setTextColor(selectedColor)
                btnPopularity.setTextColor(defaultColor)
                btnLatest.setTextColor(defaultColor)
            }

            selectedArea.doAfterTextChanged {
                detailAreaSelectLayout.visibility = View.VISIBLE
                detailSelectedArea.text = null
                onSelectArea(it.toString())
            }

            detailSelectedArea.doAfterTextChanged {
                onSelectSigungu(it.toString())
            }
        }
    }

    fun bind(itemCount: Int, item: AreaModel) {

        with(binding) {
            totalCnt.text = itemCount.toString()

            if (itemCount == 0) sortContainer.visibility = View.GONE
            else sortContainer.visibility = View.VISIBLE

            val areaAdapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_list_item_1,
                item.areas
            )
            selectedArea.setAdapter(areaAdapter)

            val sigunguAdapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_list_item_1,
                item.sigungus
            )
            detailSelectedArea.setAdapter(sigunguAdapter)
        }
    }
}