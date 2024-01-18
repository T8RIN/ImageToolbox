/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.data.saving

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.use
import ru.tech.imageresizershrinker.core.data.keys.Keys.ADD_ORIGINAL_NAME_TO_FILENAME
import ru.tech.imageresizershrinker.core.data.keys.Keys.ADD_SEQ_NUM_TO_FILENAME
import ru.tech.imageresizershrinker.core.data.keys.Keys.ADD_SIZE_TO_FILENAME
import ru.tech.imageresizershrinker.core.data.keys.Keys.COPY_TO_CLIPBOARD_MODE
import ru.tech.imageresizershrinker.core.data.keys.Keys.FILENAME_PREFIX
import ru.tech.imageresizershrinker.core.data.keys.Keys.FILENAME_SUFFIX
import ru.tech.imageresizershrinker.core.data.keys.Keys.IMAGE_PICKER_MODE
import ru.tech.imageresizershrinker.core.data.keys.Keys.OVERWRITE_FILE
import ru.tech.imageresizershrinker.core.data.keys.Keys.RANDOMIZE_FILENAME
import ru.tech.imageresizershrinker.core.data.keys.Keys.SAVE_FOLDER_URI
import ru.tech.imageresizershrinker.core.domain.image.Metadata
import ru.tech.imageresizershrinker.core.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.domain.repository.CipherRepository
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.SaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.FileParams
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.resources.R
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
    private val dataStore: DataStore<Preferences>,
    private val cipherRepository: CipherRepository
) : FileController {

    private var _fileParams: FileParams = FileParams(
        treeUri = null,
        filenamePrefix = "",
        filenameSuffix = "",
        addSizeInFilename = false,
        addOriginalFilename = false,
        addSequenceNumber = false,
        randomizeFilename = false,
        copyToClipboardMode = CopyToClipboardMode.Disabled,
        overwriteFile = false
    )

    private val fileParams get() = _fileParams

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect { preferences ->
                _fileParams = _fileParams.copy(
                    treeUri = preferences[SAVE_FOLDER_URI]?.takeIf { it.isNotEmpty() },
                    filenamePrefix = preferences[FILENAME_PREFIX] ?: "ResizedImage",
                    filenameSuffix = preferences[FILENAME_SUFFIX] ?: "",
                    addSizeInFilename = preferences[ADD_SIZE_TO_FILENAME] ?: false,
                    addOriginalFilename = preferences[ADD_ORIGINAL_NAME_TO_FILENAME] ?: false,
                    addSequenceNumber = preferences[ADD_SEQ_NUM_TO_FILENAME] ?: true,
                    randomizeFilename = preferences[RANDOMIZE_FILENAME] ?: false,
                    copyToClipboardMode = preferences[COPY_TO_CLIPBOARD_MODE]?.let {
                        CopyToClipboardMode.fromInt(it)
                    } ?: CopyToClipboardMode.Disabled,
                    overwriteFile = preferences[OVERWRITE_FILE] ?: false
                )
            }
        }
    }

    private fun Context.isExternalStorageWritable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
        else ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun getSize(uri: String): Long? = uri.toUri().fileSize(context)

    private fun Uri.fileSize(context: Context): Long? {
        context.contentResolver
            .query(this, null, null, null, null, null)
            .use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (!cursor.isNull(sizeIndex)) {
                        return cursor.getLong(sizeIndex)
                    }
                }
            }
        return null
    }

    override val savingPath: String
        get() = fileParams.treeUri?.takeIf { it.isNotEmpty() }?.toUri().toUiPath(
            context = context,
            default = context.getString(R.string.default_folder)
        )

    private fun Uri?.toUiPath(
        context: Context,
        default: String,
        isTreeUri: Boolean = true
    ): String = this?.let { uri ->
        val document = if (isTreeUri) DocumentFile.fromTreeUri(context, uri)
        else DocumentFile.fromSingleUri(context, uri)

        document?.uri?.path?.split(":")
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

    override suspend fun save(
        saveTarget: SaveTarget,
        keepMetadata: Boolean
    ): SaveResult {
        if (!context.isExternalStorageWritable()) {
            return SaveResult.Error.MissingPermissions
        }

        kotlin.runCatching {
            if (fileParams.copyToClipboardMode is CopyToClipboardMode.Enabled) {
                val clipboardManager = ContextCompat.getSystemService(
                    context,
                    ClipboardManager::class.java
                )

                cacheImage(saveTarget)?.toUri()?.let { uri ->
                    clipboardManager?.setPrimaryClip(
                        ClipData.newUri(
                            context.contentResolver,
                            "IMAGE",
                            uri
                        )
                    )
                }
            }

            if (fileParams.copyToClipboardMode is CopyToClipboardMode.Enabled.WithoutSaving) {
                return SaveResult.Success(context.getString(R.string.copied))
            }

            val originalUri = saveTarget.originalUri.toUri()
            val hasOriginalUri = runCatching {
                context.contentResolver.openFileDescriptor(originalUri, "r")
            }.isSuccess

            if (fileParams.overwriteFile && hasOriginalUri) {
                runCatching {
                    if (originalUri == Uri.EMPTY) throw IllegalStateException()

                    context.contentResolver.openFileDescriptor(originalUri, "wt")
                }.onFailure {
                    dataStore.edit {
                        it[IMAGE_PICKER_MODE] = 2
                    }
                    return SaveResult.Error.Exception(
                        Exception(
                            context.getString(
                                R.string.overwrite_file_requirements
                            )
                        )
                    )
                }.getOrNull()?.use { parcel ->
                    FileOutputStream(parcel.fileDescriptor).use { out ->
                        out.write(saveTarget.data)
                        kotlin.runCatching {
                            copyMetadata(
                                initialExif = (saveTarget as? ImageSaveTarget<*>)?.metadata as ExifInterface?,
                                fileUri = originalUri,
                                keepMetadata = keepMetadata,
                                originalUri = originalUri
                            )
                        }
                    }

                    return SaveResult.Success(
                        message = context.getString(
                            R.string.saved_to_original,
                            context.getFileName(originalUri).toString()
                        )
                    )
                }
            } else {
                fileParams.treeUri.takeIf {
                    it != null
                }?.let { treeUri ->
                    val hasDir: Boolean = treeUri.toUri().let {
                        DocumentFile.fromTreeUri(context, it)
                    }?.exists() == true

                    if (!hasDir) {
                        dataStore.edit {
                            it[SAVE_FOLDER_URI] = ""
                        }
                        return SaveResult.Error.Exception(
                            Exception(
                                context.getString(
                                    R.string.no_such_directory,
                                    treeUri.toUri().toUiPath(
                                        context,
                                        context.getString(R.string.default_folder)
                                    )
                                )
                            )
                        )
                    }
                }

                var initialExif: ExifInterface? = null

                val newSaveTarget = if (saveTarget is ImageSaveTarget<*>) {
                    initialExif = saveTarget.metadata as ExifInterface?

                    saveTarget.copy(
                        filename = constructImageFilename(saveTarget)
                    )
                } else saveTarget

                val savingFolder = context.getSavingFolder(
                    treeUri = fileParams.treeUri?.takeIf { it.isNotEmpty() }?.toUri(),
                    saveTarget = newSaveTarget
                )

                savingFolder.outputStream?.use {
                    it.write(saveTarget.data)
                }

                kotlin.runCatching {
                    copyMetadata(
                        initialExif = initialExif,
                        fileUri = savingFolder.fileUri,
                        keepMetadata = keepMetadata,
                        originalUri = saveTarget.originalUri.toUri()
                    )
                }

                val filename = newSaveTarget.filename ?: ""

                return SaveResult.Success(
                    if (savingPath.isNotEmpty() && filename.isNotEmpty()) {
                        context.getString(
                            R.string.saved_to,
                            savingPath,
                            filename
                        )
                    } else null
                )
            }
        }.onFailure {
            return SaveResult.Error.Exception(it)
        }

        return SaveResult.Error.Exception(
            SaveException(
                message = context.getString(R.string.something_went_wrong)
            )
        )
    }

    private fun copyMetadata(
        initialExif: ExifInterface?,
        fileUri: Uri?,
        keepMetadata: Boolean,
        originalUri: Uri
    ) {
        if (initialExif != null) {
            getFileDescriptorFor(fileUri)?.use {
                val ex = ExifInterface(it.fileDescriptor)
                initialExif.copyTo(ex)
                ex.saveAttributes()
            }
        } else if (keepMetadata) {
            val exif = context
                .contentResolver
                .openFileDescriptor(originalUri, "r")
                ?.use { ExifInterface(it.fileDescriptor) }

            getFileDescriptorFor(fileUri)?.use {
                val ex = ExifInterface(it.fileDescriptor)
                exif?.copyTo(ex)
                ex.saveAttributes()
            }
        } else Unit
    }

    private suspend fun cacheImage(
        saveTarget: SaveTarget
    ): String? = withContext(Dispatchers.IO) {
        val imagesFolder = File(context.cacheDir, "images")
        return@withContext kotlin.runCatching {
            imagesFolder.mkdirs()

            val file = File(
                imagesFolder,
                "${cipherRepository.generateRandomString(32)}.${saveTarget.imageFormat.extension}"
            )
            FileOutputStream(file).use {
                it.write(saveTarget.data)
            }
            FileProvider.getUriForFile(context, context.getString(R.string.file_provider), file)
                .also {
                    context.grantUriPermission(
                        context.packageName,
                        it,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
        }.getOrNull()?.toString()
    }

    private infix fun ExifInterface.copyTo(newExif: ExifInterface) {
        Metadata.metaTags.forEach { attr ->
            getAttribute(attr)?.let { newExif.setAttribute(attr, it) }
        }
        newExif.saveAttributes()
    }

    private data class SavingFolder(
        val outputStream: OutputStream? = null,
        val file: File? = null,
        val fileUri: Uri? = null
    )

    override fun constructImageFilename(
        saveTarget: ImageSaveTarget<*>
    ): String {
        val extension = saveTarget.imageInfo.imageFormat.extension

        if (fileParams.randomizeFilename) return "${cipherRepository.generateRandomString(32)}.$extension"

        val wh =
            "(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(R.string.width)
                .split(" ")[0] else saveTarget.imageInfo.width) + ")x(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(
                R.string.height
            ).split(" ")[0] else saveTarget.imageInfo.height) + ")"

        var prefix = fileParams.filenamePrefix
        var suffix = fileParams.filenameSuffix

        if (prefix.isNotEmpty()) prefix = "${prefix}_"
        if (suffix.isNotEmpty()) suffix = "_$suffix"

        if (fileParams.addOriginalFilename) {
            prefix += if (saveTarget.originalUri.toUri() != Uri.EMPTY) {
                context.getFileName(saveTarget.originalUri.toUri()) ?: ""
            } else {
                context.getString(R.string.original_filename)
            }
        }
        if (fileParams.addSizeInFilename) prefix += wh

        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date()) + "_${Random(Random.nextInt()).hashCode().toString().take(4)}"

        return "$prefix${
            if (fileParams.addSequenceNumber && saveTarget.sequenceNumber != null) {
                SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss",
                    Locale.getDefault()
                ).format(Date()) + "_" + saveTarget.sequenceNumber
            } else timeStamp
        }$suffix.$extension"
    }

    override fun clearCache(onComplete: (String) -> Unit) = context.clearCache(onComplete)

    override fun getReadableCacheSize(): String = context.cacheSize()

    private fun Context.clearCache(onComplete: (cache: String) -> Unit = {}) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    cacheDir?.deleteRecursively()
                    codeCacheDir?.deleteRecursively()
                    externalCacheDir?.deleteRecursively()
                    externalCacheDirs?.forEach {
                        it.deleteRecursively()
                    }
                }
            }
            onComplete(cacheSize())
        }
    }

    private fun Context.cacheSize(): String {
        return kotlin.runCatching {
            val cache =
                cacheDir?.walkTopDown()?.filter { it.isFile }?.map { it.length() }?.sum() ?: 0
            val code =
                codeCacheDir?.walkTopDown()?.filter { it.isFile }?.map { it.length() }?.sum() ?: 0
            var size = cache + code
            externalCacheDirs?.forEach { file ->
                size += file?.walkTopDown()?.filter { it.isFile }?.map { it.length() }?.sum() ?: 0
            }
            readableByteCount(size)
        }.getOrNull() ?: "0 B"
    }

    private fun getFileDescriptorFor(uri: Uri?) =
        uri?.let { context.contentResolver.openFileDescriptor(uri, "rw") }

    private fun Context.getSavingFolder(
        treeUri: Uri?,
        saveTarget: SaveTarget
    ): SavingFolder {
        return if (treeUri == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val type = saveTarget.imageFormat.type
                val path = "${Environment.DIRECTORY_DOCUMENTS}/ResizedImages"
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, saveTarget.filename)
                    put(
                        MediaStore.MediaColumns.MIME_TYPE,
                        type
                    )
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        path
                    )
                }
                val imageUri = contentResolver.insert(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    contentValues
                )

                SavingFolder(
                    outputStream = imageUri?.let { contentResolver.openOutputStream(it) },
                    fileUri = imageUri
                )
            } else {
                val imagesDir = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS
                    ), "ResizedImages"
                )
                if (!imagesDir.exists()) imagesDir.mkdir()
                SavingFolder(
                    outputStream = saveTarget.filename?.let {
                        FileOutputStream(File(imagesDir, it))
                    },
                    fileUri = saveTarget.filename?.let { File(imagesDir, it).toUri() }
                )
            }
        } else {
            val documentFile = DocumentFile.fromTreeUri(this, treeUri)

            if (documentFile?.exists() == false || documentFile == null) {
                throw NoSuchFileException(File(treeUri.toString()))
            }

            val file =
                documentFile.createFile(saveTarget.imageFormat.type, saveTarget.filename!!)

            val imageUri = file!!.uri
            SavingFolder(
                outputStream = contentResolver.openOutputStream(imageUri),
                fileUri = imageUri
            )
        }
    }

    private fun Uri.toPath(
        context: Context,
        isTreeUri: Boolean = true
    ): String? {
        return if (isTreeUri) {
            DocumentFile.fromTreeUri(context, this)
        } else {
            DocumentFile.fromSingleUri(context, this)
        }?.uri?.path?.split(":")?.lastOrNull()
    }

    private fun Context.getFileName(uri: Uri): String? = DocumentFile.fromSingleUri(this, uri)?.name

}