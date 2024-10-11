package kr.techit.lion.presentation.emergency.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.EmergencyMapInfo
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemEmergencyBottomBinding

class EmergencyBottomAdapter(
    private val emergencyMapInfoList: List<EmergencyMapInfo>,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<EmergencyBottomAdapter.EmergencyBottomViewHolder>() {

    class EmergencyBottomViewHolder(
        private val binding: ItemEmergencyBottomBinding,
        private val itemClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener.invoke(absoluteAdapterPosition)
            }
        }

        fun bind(item: EmergencyMapInfo) {
            with(binding) {
                if (item.emergencyType == "hospital") {
                    emergencyBottomImage.setImageResource(R.drawable.emergency_bottom_img)
                    emergencyBedLayout.visibility = View.VISIBLE
                    emergencyName.text = item.hospitalList?.hospitalName
                    emergencyAddress.text = item.hospitalList?.hospitalAddress
                    emergencyCall.text = item.hospitalList?.emergencyTel
                    emergencyBed.text =
                        item.hospitalList?.emergencyCount.toString() + " / " + item.hospitalList?.emergencyAllCount.toString()

                    item.hospitalList?.emergencyBed?.let { it ->
                        val emergencyBedIcon = emergencyBedIcon

                        val drawable = emergencyBedIcon.drawable
                        if (drawable != null) {
                            val color = when {
                                it >= 80 -> Color.parseColor("#008000") // Green
                                it >= 50 -> Color.parseColor("#FFFF00") // Yellow
                                else -> Color.parseColor("#FF0000") // Red
                            }
                            drawable.setTint(color)
                        }
                    }
                }

                if (item.emergencyType == "aed") {
                    emergencyBottomImage.setImageResource(R.drawable.aed_bottom_img)
                    emergencyBedLayout.visibility = View.GONE
                    emergencyName.text = item.aedList?.aedName
                    emergencyAddress.text = item.aedList?.aedAdress
                    emergencyCall.text = item.aedList?.aedTel
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyBottomViewHolder {
        return EmergencyBottomViewHolder(
            ItemEmergencyBottomBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemClickListener
        )

    }

    override fun getItemCount(): Int {
        return emergencyMapInfoList.size
    }

    override fun onBindViewHolder(holder: EmergencyBottomViewHolder, position: Int) {
        holder.bind(emergencyMapInfoList[position])
    }

}