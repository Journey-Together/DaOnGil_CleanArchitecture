package kr.tekit.lion.presentation.main.adapter.multi_viewholder

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemListSearchCategoryBinding
import kr.tekit.lion.presentation.ext.setClickEvent
import kr.tekit.lion.presentation.main.model.DisabilityType

class ListSearchCategoryViewHolder(
    private val binding: ItemListSearchCategoryBinding,
    private val uiScope: CoroutineScope,
    private val onClickPhysicalDisability: (DisabilityType.PhysicalDisability) -> Unit,
    private val onClickVisualImpairment: (DisabilityType.VisualImpairment) -> Unit,
    private val onClickHearingDisability: (DisabilityType.HearingImpairment) -> Unit,
    private val onClickInfantFamily: (DisabilityType.InfantFamily) -> Unit,
    private val onClickElderlyPeople: (DisabilityType.ElderlyPeople) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(optionState: MutableMap<DisabilityType, Int>) {
        fun modifyDisabilityUI(
            type: DisabilityType,
            textView: TextView,
            imageView: ImageView,
            unselectedIcon: Int,
            selectedIcon: Int,
            textResId: Int
        ) {
            val count = optionState[type] ?: 0
            val context = binding.root.context
            when (count) {
                0 -> {
                    textView.text = context.getString(textResId)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.search_view_category_name))
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, unselectedIcon))
                }
                else -> {
                    textView.text = "${context.getString(textResId)}($count)"
                    textView.setTextColor(ContextCompat.getColor(context, R.color.search_view_main))
                    imageView.setImageDrawable(ContextCompat.getDrawable(context, selectedIcon))
                }
            }
        }
        with(binding) {
            modifyDisabilityUI(
                DisabilityType.PhysicalDisability,
                textPhysicalDisability,
                imgPhysicalDisability,
                R.drawable.sv_physical_disability_unselected_icon,
                R.drawable.sv_physical_disability_selected_icon,
                R.string.text_physical_disability
            )
            //modifyDisabilityUI(DisabilityType.VisualImpairment, textVisualDisability, imgVisualDisability, R.drawable.sv_visual_disability_unselected_icon, R.drawable.sv_visual_disability_selected_icon, R.string.text_visual_impairment)
            modifyDisabilityUI(
                DisabilityType.HearingImpairment,
                textHearingDisability,
                imgHearingDisability,
                R.drawable.sv_hearing_impaired_unselected_icon,
                R.drawable.sv_hearing_impaired_selected_icon,
                R.string.text_hearing_impairment
            )
            modifyDisabilityUI(
                DisabilityType.InfantFamily,
                textInfantFamily,
                imgInfantFamily,
                R.drawable.sv_child_unselected_icon,
                R.drawable.sv_child_selected_icon,
                R.string.text_infant_family
            )
            modifyDisabilityUI(
                DisabilityType.ElderlyPeople,
                textElderlyPerson,
                imgElderlyPerson,
                R.drawable.sv_elderly_unselected_icon,
                R.drawable.sv_elderly_selected_icon,
                R.string.text_elderly_person
            )

            containerPhysicalDisability.setClickEvent(uiScope) {
                onClickPhysicalDisability(DisabilityType.PhysicalDisability)
            }
            containerVisualImpairment.setClickEvent(uiScope) {
                onClickVisualImpairment(DisabilityType.VisualImpairment)
            }
            containerHearingImpairment.setClickEvent(uiScope) {
                onClickHearingDisability(DisabilityType.HearingImpairment)
            }
            containerInfantFamily.setClickEvent(uiScope) {
                onClickInfantFamily(DisabilityType.InfantFamily)
            }
            containerElderlyPerson.setClickEvent(uiScope) {
                onClickElderlyPeople(DisabilityType.ElderlyPeople)
            }
        }
    }
}