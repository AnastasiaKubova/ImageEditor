package com.example.imageeditor.ui

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.imageeditor.R

open class BaseFragment: Fragment() {

    companion object {
        var changeFragmentListener: ChangeFragmentListener? = null
    }

    fun openCropPanel() {
        findNavController().popBackStack()
        findNavController().navigate(R.id.cropPanelFragment)
    }

    fun openMenuPanel() {
        findNavController().popBackStack()
        findNavController().navigate(R.id.mainPanelFragment)
    }

    interface ChangeFragmentListener {
        fun onChangeFragment(showHomeButton: Boolean)
    }
}