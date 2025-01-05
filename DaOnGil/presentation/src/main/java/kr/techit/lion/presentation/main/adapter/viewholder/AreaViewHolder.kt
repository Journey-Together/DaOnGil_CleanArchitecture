package kr.techit.lion.presentation.main.adapter.viewholder

import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.databinding.ItemListSearchAreaBinding
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.main.model.AreaModel

class AreaViewHolder(
    private val binding: ItemListSearchAreaBinding,
    private val onSelectArea: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.selectedArea.doAfterTextChanged {
            onSelectArea(it.toString())
        }
    }

    fun bind(item: AreaModel) {
        with(binding) {
            val areaAdapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_list_item_1,
                item.areas
            )
            selectedArea.setAdapter(areaAdapter)
            if (root.context.isTallBackEnabled()) {
                selectedArea.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    val selectedAreaMessage = "선택된 지역은 ${selectedItem}입니다."
                    selectedArea.context.announceForAccessibility(selectedAreaMessage)
                    selectedArea.context.announceForAccessibility("지역선택하단 시군구목록에서 시군구를 선택해보세요")
                }
            }
        }
    }
}