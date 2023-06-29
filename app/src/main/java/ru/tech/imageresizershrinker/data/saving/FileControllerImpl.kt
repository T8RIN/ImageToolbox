package ru.tech.imageresizershrinker.data.saving

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.data.repository.SettingsRepositoryImpl.Companion.ADD_ORIGINAL_NAME
import ru.tech.imageresizershrinker.data.repository.SettingsRepositoryImpl.Companion.ADD_SEQ_NUM
import ru.tech.imageresizershrinker.data.repository.SettingsRepositoryImpl.Companion.ADD_SIZE
import ru.tech.imageresizershrinker.data.repository.SettingsRepositoryImpl.Companion.FILENAME_PREFIX
import ru.tech.imageresizershrinker.data.repository.SettingsRepositoryImpl.Companion.SAVE_FOLDER
import ru.tech.imageresizershrinker.domain.model.BitmapSaveTarget
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveTarget
import ru.tech.imageresizershrinker.domain.saving.model.FileParams
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.copyTo
import ru.tech.imageresizershrinker.presentation.utils.helper.BitmapUtils.fileSize
import ru.tech.imageresizershrinker.presentation.utils.helper.ContextUtils.getFileName
import ru.tech.imageresizershrinker.presentation.utils.helper.ContextUtils.isExternalStorageWritable
import ru.tech.imageresizershrinker.presentation.utils.helper.ContextUtils.requestStoragePermission
import ru.tech.imageresizershrinker.presentation.utils.helper.toPath
import ru.tech.imageresizershrinker.presentation.utils.helper.toUiPath
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

class FileControllerImpl @Inject constructor(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) : FileController {

    private var fileParams: FileParams = FileParams(
        treeUri = null,
        filenamePrefix = "",
        addSizeInFilename = false,
        addOriginalFilename = false,
        addSequenceNumber = false
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect {
                fileParams = fileParams.copy(
                    treeUri = it[SAVE_FOLDER],
                    filenamePrefix = it[FILENAME_PREFIX] ?: "",
                    addSizeInFilename = it[ADD_SIZE] ?: false,
                    addOriginalFilename = it[ADD_ORIGINAL_NAME] ?: false,
                    addSequenceNumber = it[ADD_SEQ_NUM] ?: true
                )
            }
        }
    }

    override fun isExternalStorageWritable(): Boolean = context.isExternalStorageWritable()

    override fun requestReadWritePermissions() {
        context.findActivity()?.requestStoragePermission()
    }

    override fun getSize(uri: String): Long? = uri.toUri().fileSize(context)

    override val savingPath: String
        get() = fileParams.treeUri?.takeIf { it.isNotEmpty() }?.toUri().toUiPath(
            context = context,
            default = context.getString(R.string.default_folder)
        )

    override suspend fun save(
        saveTarget: SaveTarget,
        keepMetadata: Boolean
    ) {
        kotlin.runCatching {
            val savingFolder = context.getSavingFolder(
                treeUri = fileParams.treeUri?.takeIf { it.isNotEmpty() }?.toUri(),
                saveTarget = if (saveTarget is BitmapSaveTarget) {
                    saveTarget.copy(
                        filename = constructFilename(
                            context = context,
                            fileParams = fileParams,
                            saveTarget = saveTarget
                        )
                    )
                } else saveTarget
            )

            savingFolder.outputStream?.use {
                it.write(saveTarget.data)
            }

            if (keepMetadata) {
                val exif = context
                    .contentResolver
                    .openFileDescriptor(saveTarget.originalUri.toUri(), "r")
                    ?.use { ExifInterface(it.fileDescriptor) }

                getFileDescriptorFor(savingFolder.fileUri)?.use {
                    val ex = ExifInterface(it.fileDescriptor)
                    exif?.copyTo(ex)
                    ex.saveAttributes()
                }
            }
        }.getOrNull() ?: dataStore.edit { it[SAVE_FOLDER] = "" }
    }

    private data class SavingFolder(
        val outputStream: OutputStream? = null,
        val file: File? = null,
        val fileUri: Uri? = null,
    )

    private fun constructFilename(
        context: Context,
        fileParams: FileParams,
        saveTarget: BitmapSaveTarget
    ): String {
        val wh =
            "(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(R.string.width)
                .split(" ")[0] else saveTarget.bitmapInfo.width) + ")x(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(
                R.string.height
            ).split(" ")[0] else saveTarget.bitmapInfo.height) + ")"

        var prefix = fileParams.filenamePrefix
        val extension = saveTarget.bitmapInfo.mimeType.extension

        if (prefix.isEmpty()) prefix = defaultPrefix()

        if (fileParams.addOriginalFilename) prefix += "_${
            if (saveTarget.originalUri.toUri() != Uri.EMPTY) {
                context.getFileName(saveTarget.originalUri.toUri()) ?: ""
            } else {
                context.getString(R.string.original_filename)
            }
        }"
        if (fileParams.addSizeInFilename) prefix += wh

        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date()) + "_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        return "${prefix}_${
            if (fileParams.addSequenceNumber && saveTarget.sequenceNumber != null) {
                SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss",
                    Locale.getDefault()
                ).format(Date()) + "_" + saveTarget.sequenceNumber
            } else if (saveTarget.originalUri.toUri() == Uri.EMPTY && fileParams.addSequenceNumber) {
                SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss",
                    Locale.getDefault()
                ).format(Date()) + "_" + context.getString(R.string.sequence_num)
            } else {
                timeStamp
            }
        }.$extension"
    }

    private fun getFileDescriptorFor(uri: Uri?) =
        uri?.let { context.contentResolver.openFileDescriptor(uri, "rw") }

    private fun defaultPrefix() = context.getString(R.string.default_prefix)

    private fun Context.getSavingFolder(
        treeUri: Uri?,
        saveTarget: SaveTarget
    ): SavingFolder {
        return if (treeUri == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val type = saveTarget.mimeType.type
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, saveTarget.filename)
                    put(
                        MediaStore.MediaColumns.MIME_TYPE,
                        type
                    )
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "DCIM/ResizedImages"
                    )
                }
                val imageUri = contentResolver.insert(
                    if ("image" in type) {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" in type) {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" in type) {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    } else {
                        MediaStore.Files.getContentUri("external")
                    },
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
                    outputStream = FileOutputStream(File(imagesDir, saveTarget.filename!!)),
                    file = File(imagesDir, saveTarget.filename!!)
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val documentFile = DocumentFile.fromTreeUri(this, treeUri)

                if (documentFile?.exists() == false || documentFile == null) {
                    throw NoSuchFileException(File(treeUri.toString()))
                }

                val file = documentFile.createFile(saveTarget.mimeType.type, saveTarget.filename!!)

                val imageUri = file!!.uri
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
                    outputStream = FileOutputStream(File(imagesDir, saveTarget.filename!!)),
                    file = File(imagesDir, saveTarget.filename!!)
                )
            }
        }
    }

    private fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

}