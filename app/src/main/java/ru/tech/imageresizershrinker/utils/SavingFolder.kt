package ru.tech.imageresizershrinker.utils

import android.net.Uri
import java.io.File
import java.io.OutputStream

data class SavingFolder(
    val outputStream: OutputStream? = null,
    val file: File? = null,
    val fileUri: Uri? = null,
)