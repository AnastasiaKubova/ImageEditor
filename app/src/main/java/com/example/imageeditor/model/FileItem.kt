package com.example.imageeditor.model

import android.net.Uri
import com.example.imageeditor.enum.FileItemType
import java.util.*

data class FileItem(val name: String, val data: Date, val uri: Uri, val type: FileItemType) {
}