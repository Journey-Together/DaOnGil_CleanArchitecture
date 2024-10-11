package kr.techit.lion.presentation.emergency.fragment

import android.content.Context
import android.content.Intent
import kr.techit.lion.domain.model.PharmacyMapInfo
import kr.techit.lion.presentation.databinding.EmergencyBottomSheetLayoutBinding
import kr.techit.lion.presentation.emergency.EmergencyInfoActivity
import kr.techit.lion.presentation.emergency.adapter.PharmacyBottomAdapter
import kr.techit.lion.presentation.model.toPharmacyInfo

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