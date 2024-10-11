package kr.techit.lion.presentation.emergency.fragment

import android.content.Context
import android.content.Intent
import kr.techit.lion.domain.model.EmergencyMapInfo
import kr.techit.lion.presentation.databinding.EmergencyBottomSheetLayoutBinding
import kr.techit.lion.presentation.emergency.EmergencyInfoActivity
import kr.techit.lion.presentation.emergency.adapter.EmergencyBottomAdapter
import kr.techit.lion.presentation.model.toEmergencyInfo

class EmergencyBottomSheet(
    private val binding: EmergencyBottomSheetLayoutBinding,
    private val emergencyMapInfoList: List<EmergencyMapInfo>
) {

    private val emergencyBottomAdapter: EmergencyBottomAdapter by lazy {
        EmergencyBottomAdapter(emergencyMapInfoList,
            itemClickListener = { position ->
                val context: Context = binding.root.context
                val intent = Intent(context, EmergencyInfoActivity::class.java)
                val emergencyInfoList = emergencyMapInfoList.map {
                    it.toEmergencyInfo()
                }
                intent.putExtra("infoType", emergencyMapInfoList[position].emergencyType)
                intent.putExtra("data",emergencyInfoList[position])
                intent.putExtra("id", emergencyMapInfoList[position].emergencyId)
                context.startActivity(intent)
            }
        )
    }

    fun setRecyclerView(){
        with(binding){
            emergencyBottomRv.adapter = emergencyBottomAdapter
            emergencyBottomAdapter.notifyDataSetChanged()
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