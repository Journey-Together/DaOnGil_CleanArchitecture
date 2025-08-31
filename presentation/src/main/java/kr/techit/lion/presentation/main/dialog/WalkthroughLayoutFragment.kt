package kr.techit.lion.presentation.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

class WalkthroughLayoutFragment : Fragment() {
    companion object {
        private const val KEY = "layoutRes"

        fun newInstance(@LayoutRes layout: Int) =
            WalkthroughLayoutFragment().apply { arguments = bundleOf(KEY to layout) }
    }
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        i.inflate(requireArguments().getInt("layoutRes"), c, false)
}