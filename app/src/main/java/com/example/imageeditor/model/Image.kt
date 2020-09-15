package com.example.imageeditor.model

import android.net.Uri
import java.util.*

data class Image(val name: String, val data: Date, val uri: Uri) {
}