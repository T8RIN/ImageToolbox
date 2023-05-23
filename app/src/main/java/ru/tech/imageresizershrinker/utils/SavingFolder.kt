package ru.tech.imageresizershrinker.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.single_resize_screen.components.extension
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.getFileName
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            val startPath = if (
                uri.toString()
                    .split("%")[0]
                    .contains("primary")
            ) context.getString(R.string.device_storage)
            else context.getString(R.string.external_storage)

            startPath + endPath
        }
} ?: default

fun defaultPrefix() = "ResizedImage"

fun constructFilename(
    context: Context,
    fileParams: FileParams,
    saveTarget: SaveTarget
): String {
    val wh = "(" + (if (saveTarget.uri == Uri.EMPTY) context.getString(R.string.width)
        .split(" ")[0] else saveTarget.bitmapInfo.width) + ")x(" + (if (saveTarget.uri == Uri.EMPTY) context.getString(
        R.string.height
    ).split(" ")[0] else saveTarget.bitmapInfo.height) + ")"

    var prefix = fileParams.filenamePrefix
    val extension = saveTarget.bitmapInfo.mimeTypeInt.extension

    if (prefix.isEmpty()) prefix = defaultPrefix()

    if (fileParams.addOriginalFilename) prefix += "_${
        if (saveTarget.uri != Uri.EMPTY) context.getFileName(
            saveTarget.uri
        ) ?: "" else context.getString(R.string.original_filename)
    }"
    if (fileParams.addSizeInFilename) prefix += wh

    val timeStamp = SimpleDateFormat(
        "yyyy-MM-dd_HH-mm-ss_ms",
        Locale.getDefault()
    ).format(Date())
    return "${prefix}_${
        if (fileParams.addSequenceNumber && saveTarget.sequenceNumber != null) saveTarget.sequenceNumber else if (saveTarget.uri == Uri.EMPTY && fileParams.addSequenceNumber) context.getString(
            R.string.sequence_num
        ) else timeStamp
    }.$extension"
}

fun Context.getSavingFolder(
    treeUri: Uri?,
    extension: String,
    filename: String,
): SavingFolder {
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