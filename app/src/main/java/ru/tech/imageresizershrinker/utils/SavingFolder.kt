package ru.tech.imageresizershrinker.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import ru.tech.imageresizershrinker.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

data class SavingFolder(
    val outputStream: OutputStream? = null,
    val file: File? = null,
    val fileUri: Uri? = null,
)

fun Uri.toPath(
    context: Context,
    isTreeUri: Boolean = true
): String? {
    return if (isTreeUri) {
        DocumentFile.fromTreeUri(context, this)
    } else {
        DocumentFile.fromSingleUri(context, this)
    }?.uri?.path?.split(":")?.lastOrNull()
}

fun Uri?.toUiPath(context: Context, default: String): String = this?.let { uri ->
    DocumentFile
        .fromTreeUri(context, uri)
        ?.uri?.path?.split(":")
        ?.lastOrNull()?.let { p ->
            val endPath = p.takeIf {
                it.isNotEmpty()
            }?.let { "/$it" } ?: ""
            val startPath = if ("primary" in uri.toString()
                    .split("%")[0]
            ) context.getString(R.string.device_storage)
            else context.getString(R.string.external_storage)

            startPath + endPath
        }
} ?: default

fun Context.getSavingFolder(treeUri: Uri?, filename: String, extension: String): SavingFolder {
    return if (treeUri == null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "image/$extension"
                )
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "DCIM/ResizedImages"
                )
            }
            val imageUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            SavingFolder(
                outputStream = contentResolver.openOutputStream(imageUri!!),
                fileUri = imageUri
            )
        } else {
            val imagesDir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                ), "ResizedImages"
            )
            if (!imagesDir.exists()) imagesDir.mkdir()
            SavingFolder(
                outputStream = FileOutputStream(File(imagesDir, filename)),
                file = File(imagesDir, filename)
            )
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val imageUri = DocumentFile
                .fromTreeUri(this, treeUri)
                ?.createFile("image/$extension", filename)?.uri!!

            SavingFolder(
                outputStream = contentResolver.openOutputStream(imageUri),
                fileUri = imageUri
            )
        } else {
            val path = treeUri.toPath(this@getSavingFolder)?.split("/")?.let {
                it - it.last() to it.last()
            }
            val imagesDir = File(
                Environment.getExternalStoragePublicDirectory(
                    "${path?.first?.joinToString("/")}"
                ), path?.second.toString()
            )
            if (!imagesDir.exists()) imagesDir.mkdir()
            SavingFolder(
                outputStream = FileOutputStream(File(imagesDir, filename)),
                file = File(imagesDir, filename)
            )
        }
    }
}