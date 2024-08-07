package kr.tekit.lion.presentation.main.adapter.viewholder

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.ItemListSearchCategoryBinding
import kr.tekit.lion.presentation.ext.setClickEvent
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.VisualImpairment

class ListSearchCategoryViewHolder(
    private val binding: ItemListSearchCategoryBinding,
    private val uiScope: CoroutineScope,
    private val onClickPhysicalDisability: (PhysicalDisability) -> Unit,
    private val onClickVisualImpairment: (VisualImpairment) -> Unit,
    private val onClickHearingDisability: (HearingImpairment) -> Unit,
    private val onClickInfantFamily: (InfantFamily) -> Unit,
    private val onClickElderlyPeople: (ElderlyPeople) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        with(binding) {
            containerPhysicalDisability.setClickEvent(uiScope) {
                onClickPhysicalDisability(PhysicalDisability)
            }
            containerVisualImpairment.setClickEvent(uiScope) {
                onClickVisualImpairment(VisualImpairment)
            }
            containerHearingImpairment.setClickEvent(uiScope) {
                onClickHearingDisability(HearingImpairment)
            }
            containerInfantFamily.setClickEvent(uiScope) {
                onClickInfantFamily(InfantFamily)
            }
            containerElderlyPerson.setClickEvent(uiScope) {
                onClickElderlyPeople(ElderlyPeople)
            }
        }
    }

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
                    textView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.search_view_category_name
                        )
                    )
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
                PhysicalDisability,
                textPhysicalDisability,
                imgPhysicalDisability,
                R.drawable.sv_physical_disability_unselected_icon,
                R.drawable.sv_physical_disability_selected_icon,
                R.string.text_physical_disability
            )
            modifyDisabilityUI(
                VisualImpairment,
                textVisualDisability,
                imgVisualDisability,
                R.drawable.sv_visual_impairment_unselect_icon,
                R.drawable.sv_visual_impairment_select_icon,
                R.string.text_visual_impairment
            )
            modifyDisabilityUI(
                HearingImpairment,
                textHearingDisability,
                imgHearingDisability,
                R.drawable.sv_hearing_impaired_unselected_icon,
                R.drawable.sv_hearing_impaired_selected_icon,
                R.string.text_hearing_impairment
            )
            modifyDisabilityUI(
                InfantFamily,
                textInfantFamily,
                imgInfantFamily,
                R.drawable.sv_child_unselected_icon,
                R.drawable.sv_child_selected_icon,
                R.string.text_infant_family
            )
            modifyDisabilityUI(
                ElderlyPeople,
                textElderlyPerson,
                imgElderlyPerson,
                R.drawable.sv_elderly_unselected_icon,
                R.drawable.sv_elderly_selected_icon,
                R.string.text_elderly_person
            )
        }
    }
}