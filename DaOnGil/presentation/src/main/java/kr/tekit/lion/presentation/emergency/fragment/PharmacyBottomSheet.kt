package kr.tekit.lion.presentation.emergency.fragment

import android.content.Context
import android.content.Intent
import kr.tekit.lion.domain.model.PharmacyMapInfo
import kr.tekit.lion.presentation.databinding.EmergencyBottomSheetLayoutBinding
import kr.tekit.lion.presentation.emergency.EmergencyInfoActivity
import kr.tekit.lion.presentation.emergency.adapter.PharmacyBottomAdapter
import kr.tekit.lion.presentation.model.toPharmacyInfo

class PharmacyBottomSheet(
    private val binding: EmergencyBottomSheetLayoutBinding,
    private val pharmacyBottomList: List<PharmacyMapInfo>
) {
    private val pharmacyBottomAdapter: PharmacyBottomAdapter by lazy {
        PharmacyBottomAdapter(pharmacyBottomList,
            itemClickListener = { position ->
                val context: Context = binding.root.context
                val intent = Intent(context, EmergencyInfoActivity::class.java)
                val pharmacyInfo = pharmacyBottomList[position].toPharmacyInfo()
                intent.putExtra("infoType", "pharmacy")
                intent.putExtra("data", pharmacyInfo)
                context.startActivity(intent)
            })
    }

    fun setRecyclerView(){
        with(binding){
            emergencyBottomRv.adapter = pharmacyBottomAdapter
            pharmacyBottomAdapter.notifyDataSetChanged()
        }
    }

    fun recyclerViewTopButton() {
        with(binding){
            emergencyBottomSheetHead.setOnClickListener {
                emergencyBottomRv.scrollToPosition(0)
            }
        }
    }
}