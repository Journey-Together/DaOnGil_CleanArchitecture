package kr.tekit.lion.presentation.main.adapter.viewholder

import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.presentation.databinding.ItemListSearchAreaBinding
import kr.tekit.lion.presentation.main.model.AreaModel

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
        }
    }
}