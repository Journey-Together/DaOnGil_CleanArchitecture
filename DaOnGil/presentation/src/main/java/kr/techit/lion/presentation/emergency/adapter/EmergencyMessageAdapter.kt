package kr.techit.lion.presentation.emergency.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.techit.lion.domain.model.EmergencyMessageInfo
import kr.techit.lion.presentation.databinding.ItemEmergencyMessageBinding

class EmergencyMessageAdapter(private val emergencyMessageList: List<EmergencyMessageInfo>) :
    RecyclerView.Adapter<EmergencyMessageAdapter.EmergencyMessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyMessageViewHolder {
        return EmergencyMessageViewHolder(
            ItemEmergencyMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: EmergencyMessageViewHolder,
        position: Int
    ) {
        holder.bind(emergencyMessageList[position])
    }

    override fun getItemCount(): Int {
        return emergencyMessageList.size
    }

    class EmergencyMessageViewHolder(private val binding: ItemEmergencyMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EmergencyMessageInfo) {
            binding.emergencyMessageText.text = item.emergencyMessage
            binding.emergencyMessageDate.text = item.emergencyDate
        }
    }

}