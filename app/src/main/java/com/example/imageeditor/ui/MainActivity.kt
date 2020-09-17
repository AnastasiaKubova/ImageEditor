package com.example.imageeditor.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.imageeditor.R
import com.example.imageeditor.ui.imagepicker.ImagePickerFragment

class MainActivity : AppCompatActivity() {

    companion object {
        var listener: MainActivityListener? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        if (navController.backStack.count() == 0) {
            super.onBackPressed()
        } else {
            listener?.onBackPressed()
        }
    }

    interface MainActivityListener {
        fun onBackPressed()
    }
}