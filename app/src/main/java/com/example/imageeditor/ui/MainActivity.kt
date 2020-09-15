package com.example.imageeditor.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.imageeditor.R
import com.example.imageeditor.ui.editor.ImageEditorFragment

class MainActivity : AppCompatActivity() {

    private lateinit var currentFragment: BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Init start fragment. */
        currentFragment = ImageEditorFragment()
        changeFragment()
    }

    private fun changeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, currentFragment)
            .commitNow()
    }
}