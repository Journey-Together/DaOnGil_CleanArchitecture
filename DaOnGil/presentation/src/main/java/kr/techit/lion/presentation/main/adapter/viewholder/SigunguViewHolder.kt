package kr.techit.lion.presentation.main.adapter.viewholder

import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.presentation.databinding.ItemListSearchSigunguBinding
import kr.techit.lion.presentation.ext.announceForAccessibility
import kr.techit.lion.presentation.ext.isTallBackEnabled
import kr.techit.lion.presentation.main.model.SigunguModel

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
            detailSelectedArea.setText(item.selectedSigungu)
            val sigunguAdapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_list_item_1,
                item.sigungus
            )
            detailSelectedArea.setAdapter(sigunguAdapter)
            if (root.context.isTallBackEnabled()){
                detailSelectedArea.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    val selectedAreaMessage = "선택된 지역은 ${selectedItem}입니다."
                    detailSelectedArea.context.announceForAccessibility(selectedAreaMessage)
                }
            }
        }
    }
}