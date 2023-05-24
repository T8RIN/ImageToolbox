package ru.tech.imageresizershrinker.utils.storage

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.fileSize
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.utils.helper.extension

private class FileControllerImpl constructor(
    private val context: Context,
    override val fileParams: FileParams
) : FileController {

    override fun getSavingFolder(
        saveTarget: SaveTarget
    ): SavingFolder = context.getSavingFolder(
        treeUri = fileParams.treeUri,
        filename = constructFilename(
            context = context,
            fileParams = fileParams,
            saveTarget = saveTarget
        ),
        extension = saveTarget.bitmapInfo.mimeTypeInt.extension
    )

    override fun getFileDescriptorFor(uri: Uri?) = uri?.let {
        context.contentResolver.openFileDescriptor(it, "rw", null)
    }

    override fun isExternalStorageWritable(): Boolean = context.isExternalStorageWritable()

    override fun requestReadWritePermissions() {
        context.findActivity()?.requestStoragePermission()
    }

    override fun getSize(uri: Uri?): Long? = uri?.fileSize(context)

    override val savingPath: String
        get() = fileParams.treeUri.toUiPath(
            context = context,
            default = context.getString(R.string.default_folder)
        )

    private fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

}

interface FileController {
    val fileParams: FileParams
    val savingPath: String
    fun getSavingFolder(saveTarget: SaveTarget): SavingFolder
    fun getFileDescriptorFor(uri: Uri?): ParcelFileDescriptor?
    fun isExternalStorageWritable(): Boolean
    fun requestReadWritePermissions()
    fun getSize(uri: Uri?): Long?
}

data class FileParams(
    val treeUri: Uri?,
    val filenamePrefix: String,
    val addSizeInFilename: Boolean,
    val addOriginalFilename: Boolean,
    val addSequenceNumber: Boolean
)

data class SaveTarget(
    val bitmapInfo: BitmapInfo,
    val uri: Uri,
    val sequenceNumber: Int?
)

val LocalFileController = compositionLocalOf<FileController> { error("FileController not present") }

fun FileController(
    context: Context,
    fileParams: FileParams
): FileController = FileControllerImpl(context, fileParams)

@Composable
fun rememberFileController(
    context: Context,
    fileParams: FileParams
): FileController = remember(context, fileParams) {
    derivedStateOf {
        FileController(
            context = context,
            fileParams = fileParams
        )
    }
}.value