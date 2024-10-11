package kr.techit.lion.presentation.emergency.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.PharmacyMapInfo
import kr.techit.lion.presentation.R
import kr.techit.lion.presentation.databinding.ItemEmergencyBottomBinding

class PharmacyBottomAdapter(
    private val pharmacyBottomList: List<PharmacyMapInfo>,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<PharmacyBottomAdapter.PharmacyBottomViewHolder>(){

    class PharmacyBottomViewHolder(
        private val binding: ItemEmergencyBottomBinding,
        private val itemClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                itemClickListener.invoke(absoluteAdapterPosition)
            }
        }

        fun bind(item: PharmacyMapInfo) {
            with(binding) {
                emergencyBottomImage.setImageResource(R.drawable.pharmacy_bottom_img)
                emergencyName.text = item.pharmacyName
                emergencyAddress.text = item.pharmacyAddress
                emergencyCall.text = item.pharmacyTel
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyBottomViewHolder {
        return PharmacyBottomViewHolder(
            ItemEmergencyBottomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            itemClickListener
        )
    }

    override fun getItemCount(): Int {
        return pharmacyBottomList.size
    }

    override fun onBindViewHolder(holder: PharmacyBottomViewHolder, position: Int) {
        holder.bind(pharmacyBottomList[position])
    }
}