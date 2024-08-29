package kr.tekit.lion.presentation.main.bottomsheet

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kr.tekit.lion.presentation.R
import kr.tekit.lion.presentation.databinding.CategoryBottomSheetLayoutBinding
import kr.tekit.lion.presentation.ext.announceForAccessibility
import kr.tekit.lion.presentation.ext.isTallBackEnabled
import kr.tekit.lion.presentation.main.model.AudioGuide
import kr.tekit.lion.presentation.main.model.BabySpareChair
import kr.tekit.lion.presentation.main.model.Braileblock
import kr.tekit.lion.presentation.main.model.DisabilityType
import kr.tekit.lion.presentation.main.model.Elevator
import kr.tekit.lion.presentation.main.model.Guide
import kr.tekit.lion.presentation.main.model.HelpDog
import kr.tekit.lion.presentation.main.model.LactationRoom
import kr.tekit.lion.presentation.main.model.Parking
import kr.tekit.lion.presentation.main.model.PhysicalDisability
import kr.tekit.lion.presentation.main.model.RestRoom
import kr.tekit.lion.presentation.main.model.Seat
import kr.tekit.lion.presentation.main.model.SignGuide
import kr.tekit.lion.presentation.main.model.Stroller
import kr.tekit.lion.presentation.main.model.VideoGuide
import kr.tekit.lion.presentation.main.model.Wheelchair
import kr.tekit.lion.presentation.main.model.WheelchairLent
import kr.tekit.lion.presentation.main.model.ElderlyPeople
import kr.tekit.lion.presentation.main.model.HearingImpairment
import kr.tekit.lion.presentation.main.model.InfantFamily
import kr.tekit.lion.presentation.main.model.VisualImpairment

class CategoryBottomSheet(
    private val selectedOption: List<Int>,
    private val selectedCategory: DisabilityType,
    private val itemClick: (List<Int>, List<Long>) -> Unit
) : BottomSheetDialogFragment(R.layout.category_bottom_sheet_layout) {
    private val selectedOptions = ArrayList<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = CategoryBottomSheetLayoutBinding.bind(view)

        initChips(binding)
        setOptions(binding)
    }

    private fun initChips(binding: CategoryBottomSheetLayoutBinding) {
        if (selectedOption.isNotEmpty()) {
            selectedOption.map {
                when (selectedCategory) {
                    PhysicalDisability -> {
                        val chip = binding.chipGroupPhysicalDisability.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    HearingImpairment -> {
                        val chip = binding.chipGroupHearingImpairment.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    VisualImpairment -> {
                        val chip = binding.chipGroupVisualImpairment.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    InfantFamily -> {
                        val chip = binding.chipGroupInfantFamily.findViewById<Chip>(it)
                        chip.isChecked = true
                    }

                    ElderlyPeople -> {
                        val chip = binding.chipGroupElderlyPerson.findViewById<Chip>(it)
                        chip.isChecked = true
                    }
                }
            }
        }
    }


    private fun setOptions(binding: CategoryBottomSheetLayoutBinding) {
        with(binding) {
            when (selectedCategory) {
                PhysicalDisability -> {
                    chipGroupPhysicalDisability.visibility = View.VISIBLE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE
                }

                HearingImpairment -> {
                    chipGroupHearingImpairment.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE
                }

                VisualImpairment -> {
                    chipGroupVisualImpairment.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE
                }

                InfantFamily -> {
                    chipGroupInfantFamily.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupElderlyPerson.visibility = View.GONE
                }

                ElderlyPeople -> {
                    chipGroupElderlyPerson.visibility = View.VISIBLE
                    chipGroupPhysicalDisability.visibility = View.GONE
                    chipGroupVisualImpairment.visibility = View.GONE
                    chipGroupHearingImpairment.visibility = View.GONE
                    chipGroupInfantFamily.visibility = View.GONE
                }
            }

            allChip.setOnClickListener {
                selectedOptions.clear()
                if (allChip.isChecked) allChip.isChecked = false

                when (selectedCategory) {
                    PhysicalDisability -> {
                        chipGroupPhysicalDisability.check(R.id.wheelchair_chip)
                        chipGroupPhysicalDisability.check(R.id.parking_chip)
                        chipGroupPhysicalDisability.check(R.id.elevator_chip)
                        chipGroupPhysicalDisability.check(R.id.restroom_chip)
                        chipGroupPhysicalDisability.check(R.id.seat_chip)
                    }

                    VisualImpairment -> {
                        chipGroupVisualImpairment.check(R.id.braileblock_chip)
                        chipGroupVisualImpairment.check(R.id.help_dog_chip)
                        chipGroupVisualImpairment.check(R.id.guide_chip)
                        chipGroupVisualImpairment.check(R.id.audio_guide_chip)
                    }

                    HearingImpairment -> {
                        chipGroupHearingImpairment.check(R.id.sign_guide_chip)
                        chipGroupHearingImpairment.check(R.id.video_guide_chip)
                    }

                    InfantFamily -> {
                        chipGroupInfantFamily.check(R.id.stroller_chip)
                        chipGroupInfantFamily.check(R.id.lactation_room_chip)
                        chipGroupInfantFamily.check(R.id.baby_spare_chair_chip)
                    }

                    ElderlyPeople -> {
                        chipGroupElderlyPerson.check(R.id.lend_chip)
                    }
                }
                if (requireContext().isTallBackEnabled()){
                    requireContext().announceForAccessibility(getString(R.string.text_select_all_option))
                }
            }

            btnReset.setOnClickListener {
                allChip.isChecked = false
                when (selectedCategory) {
                    PhysicalDisability -> {
                        chipGroupPhysicalDisability.clearCheck()
                    }
                    HearingImpairment -> {
                        chipGroupHearingImpairment.clearCheck()
                    }
                    VisualImpairment -> {
                        chipGroupVisualImpairment.clearCheck()
                    }
                    InfantFamily -> {
                        chipGroupInfantFamily.clearCheck()
                    }
                    ElderlyPeople -> {
                        chipGroupElderlyPerson.clearCheck()
                    }
                }
                if (requireContext().isTallBackEnabled()){
                    requireContext().announceForAccessibility(getString(R.string.text_reset_all_option))
                }
            }

            btnSubmit.setOnClickListener {
                allChip.isChecked = false
                val selectedChipIds = when (selectedCategory) {
                    is PhysicalDisability -> {
                        chipGroupPhysicalDisability.checkedChipIds
                    }

                    is HearingImpairment -> {
                        chipGroupHearingImpairment.checkedChipIds
                    }

                    is VisualImpairment -> {
                        chipGroupVisualImpairment.checkedChipIds
                    }

                    is InfantFamily -> {
                        chipGroupInfantFamily.checkedChipIds
                    }

                    is ElderlyPeople -> {
                        chipGroupElderlyPerson.checkedChipIds
                    }
                }

                val selectedChipTexts = selectedChipIds.map { chipId ->
                    when(chipId){
                        R.id.parking_chip -> Parking.code
                        R.id.wheelchair_chip -> Wheelchair.code
                        R.id.restroom_chip -> RestRoom.code
                        R.id.elevator_chip -> Elevator.code
                        R.id.seat_chip -> Seat.code
                        R.id.braileblock_chip -> Braileblock.code
                        R.id.help_dog_chip -> HelpDog.code
                        R.id.guide_chip -> Guide.code
                        R.id.audio_guide_chip -> AudioGuide.code
                        R.id.sign_guide_chip -> SignGuide.code
                        R.id.video_guide_chip -> VideoGuide.code
                        R.id.stroller_chip -> Stroller.code
                        R.id.lactation_room_chip -> LactationRoom.code
                        R.id.baby_spare_chair_chip -> BabySpareChair.code
                        else -> WheelchairLent.code
                    }
                }
                itemClick(selectedChipIds, selectedChipTexts)
                dismiss()
            }

        }
    }

    override fun getTheme(): Int {
        return R.style.category_bottom_sheet_dialog_theme
    }
}
