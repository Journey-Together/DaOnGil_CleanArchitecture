package kr.tekit.lion.presentation.main.adapter.viewholder

import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import kr.tekit.lion.presentation.databinding.ItemListSearchSigunguBinding
import kr.tekit.lion.presentation.main.model.SigunguModel

class SigunguViewHolder(
    private val binding: ItemListSearchSigunguBinding,
    private val onSelectSigungu: (String) -> Unit,
): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.detailSelectedArea.doAfterTextChanged {
            val sigungu = it.toString()
            if (sigungu != "시/군/구") onSelectSigungu(sigungu)
        }
    }

    fun bind(item: SigunguModel){
        with(binding){
            detailSelectedArea.setText("시/군/구")
            val sigunguAdapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_list_item_1,
                item.sigungus
            )
            binding.detailSelectedArea.setAdapter(sigunguAdapter)
        }
    }
}