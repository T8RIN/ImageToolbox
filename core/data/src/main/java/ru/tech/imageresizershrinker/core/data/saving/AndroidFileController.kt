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
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.use
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.MetadataTag
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.RandomStringGenerator
import ru.tech.imageresizershrinker.core.domain.saving.Writeable
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.use
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.OneTimeSaveLocation
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random


internal class AndroidFileController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val randomStringGenerator: RandomStringGenerator,
    private val shareProvider: ShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, FileController {

    private var _settingsState: SettingsState = SettingsState.Default

    private val settingsState get() = _settingsState

    init {
        settingsManager
            .getSettingsStateFlow()
            .onEach { state ->
                _settingsState = state
            }.launchIn(CoroutineScope(defaultDispatcher))
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

    private fun String?.getPath() = this?.takeIf { it.isNotEmpty() }?.toUri().toUiPath(
        context = context,
        default = context.getString(R.string.default_folder)
    )

    override val defaultSavingPath: String
        get() = settingsState.saveFolderUri.getPath()

    private fun Uri?.toUiPath(
        context: Context,
        default: String,
        isTreeUri: Boolean = true
    ): String = this?.let { uri ->
        runCatching {
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
        }.getOrNull()
    } ?: default

    @SuppressLint("StringFormatInvalid")
    override suspend fun save(
        saveTarget: SaveTarget,
        keepOriginalMetadata: Boolean,
        oneTimeSaveLocationUri: String?
    ): SaveResult = withContext(ioDispatcher) {
        if (!context.isExternalStorageWritable()) {
            return@withContext SaveResult.Error.MissingPermissions
        }

        val savingPath = oneTimeSaveLocationUri?.getPath() ?: defaultSavingPath

        runCatching {
            if (settingsState.copyToClipboardMode is CopyToClipboardMode.Enabled) {
                val clipboardManager = context.getSystemService<ClipboardManager>()

                shareProvider.cacheByteArray(
                    byteArray = saveTarget.data,
                    filename = "${randomStringGenerator.generate(32)}.${saveTarget.extension}"
                )?.toUri()?.let { uri ->
                    clipboardManager?.setPrimaryClip(
                        ClipData.newUri(
                            context.contentResolver,
                            "IMAGE",
                            uri
                        )
                    )
                }
            }

            if (settingsState.copyToClipboardMode is CopyToClipboardMode.Enabled.WithoutSaving) {
                return@withContext SaveResult.Success(
                    message = context.getString(R.string.copied),
                    savingPath = savingPath
                )
            }

            val originalUri = saveTarget.originalUri.toUri()
            val hasOriginalUri = runCatching {
                context.contentResolver.openFileDescriptor(originalUri, "r")
            }.isSuccess

            if (settingsState.overwriteFiles && hasOriginalUri) {
                runCatching {
                    if (originalUri == Uri.EMPTY) throw IllegalStateException()

                    context.contentResolver.openFileDescriptor(originalUri, "wt")
                }.onFailure {
                    settingsManager.setImagePickerMode(3)
                    return@withContext SaveResult.Error.Exception(
                        Exception(
                            context.getString(
                                R.string.overwrite_file_requirements
                            )
                        )
                    )
                }.getOrNull()?.use { parcel ->
                    FileOutputStream(parcel.fileDescriptor).use { out ->
                        out.write(saveTarget.data)
                        runCatching {
                            copyMetadata(
                                initialExif = (saveTarget as? ImageSaveTarget<*>)?.metadata as ExifInterface?,
                                fileUri = originalUri,
                                keepMetadata = keepOriginalMetadata,
                                originalUri = originalUri
                            )
                        }
                    }

                    return@withContext SaveResult.Success(
                        message = context.getString(
                            R.string.saved_to_original,
                            originalUri.getFilename().toString()
                        ),
                        savingPath = savingPath
                    )
                }
            } else {
                val documentFile: DocumentFile?
                val treeUri = (oneTimeSaveLocationUri ?: settingsState.saveFolderUri).takeIf {
                    !it.isNullOrEmpty()
                }

                if (treeUri != null) {
                    documentFile = runCatching {
                        treeUri.toUri().let {
                            if (DocumentFile.isDocumentUri(context, it)) {
                                DocumentFile.fromSingleUri(context, it)
                            } else DocumentFile.fromTreeUri(context, it)
                        }
                    }.getOrNull()

                    if (documentFile?.exists() == false && documentFile.isDirectory || documentFile == null) {
                        if (oneTimeSaveLocationUri == null) {
                            settingsManager.setSaveFolderUri(null)
                        } else {
                            settingsManager.setOneTimeSaveLocations(
                                settingsState.oneTimeSaveLocations.let { locations ->
                                    (locations - locations.find { it.uri == oneTimeSaveLocationUri }).filterNotNull()
                                }
                            )
                        }
                        return@withContext SaveResult.Error.Exception(
                            Exception(
                                context.getString(
                                    R.string.no_such_directory,
                                    treeUri.toUri().toUiPath(context, treeUri)
                                )
                            )
                        )
                    }
                } else {
                    documentFile = null
                }

                var initialExif: ExifInterface? = null

                val newSaveTarget = if (saveTarget is ImageSaveTarget<*>) {
                    initialExif = saveTarget.metadata as ExifInterface?

                    saveTarget.copy(
                        filename = constructImageFilename(
                            saveTarget = saveTarget,
                            forceNotAddSizeInFilename = saveTarget.imageInfo.height <= 0 || saveTarget.imageInfo.width <= 0
                        )
                    )
                } else saveTarget

                val savingFolder = getSavingFolder(
                    treeUri = treeUri?.toUri(),
                    saveTarget = newSaveTarget
                )

                savingFolder.outputStream?.use {
                    it.write(saveTarget.data)
                }

                runCatching {
                    copyMetadata(
                        initialExif = initialExif,
                        fileUri = savingFolder.fileUri,
                        keepMetadata = keepOriginalMetadata,
                        originalUri = saveTarget.originalUri.toUri()
                    )
                }

                val filename = newSaveTarget.filename ?: ""

                oneTimeSaveLocationUri?.let {
                    if (documentFile?.isDirectory == true) {
                        val currentLocation =
                            settingsState.oneTimeSaveLocations.find { it.uri == oneTimeSaveLocationUri }

                        settingsManager.setOneTimeSaveLocations(
                            currentLocation?.let {
                                settingsState.oneTimeSaveLocations.toMutableList().apply {
                                    remove(currentLocation)
                                    add(
                                        currentLocation.copy(
                                            uri = oneTimeSaveLocationUri,
                                            date = System.currentTimeMillis(),
                                            count = currentLocation.count + 1
                                        )
                                    )
                                }
                            } ?: settingsState.oneTimeSaveLocations.plus(
                                OneTimeSaveLocation(
                                    uri = oneTimeSaveLocationUri,
                                    date = System.currentTimeMillis(),
                                    count = 1
                                )
                            )
                        )
                    }
                }

                return@withContext SaveResult.Success(
                    message = if (savingPath.isNotEmpty()) {
                        val isFile = documentFile?.isDirectory != true
                        if (isFile) {
                            context.getString(R.string.saved_to_custom)
                        } else if (filename.isNotEmpty()) {
                            context.getString(
                                R.string.saved_to,
                                savingPath,
                                filename
                            )
                        } else {
                            context.getString(
                                R.string.saved_to_without_filename,
                                savingPath
                            )
                        }
                    } else null,
                    savingPath = savingPath
                )
            }
        }.onFailure {
            it.makeLog()
            return@withContext SaveResult.Error.Exception(it)
        }

        return@withContext SaveResult.Error.Exception(
            SaveException(
                message = context.getString(R.string.something_went_wrong)
            )
        )
    }

    private suspend fun copyMetadata(
        initialExif: ExifInterface?,
        fileUri: Uri?,
        keepMetadata: Boolean,
        originalUri: Uri
    ) = withContext(ioDispatcher) {
        if (initialExif != null) {
            getFileDescriptorFor(fileUri)?.use {
                val ex = ExifInterface(it.fileDescriptor)
                initialExif.copyTo(ex)
                ex.saveAttributes()
            }
        } else if (keepMetadata) {
            val newUri = originalUri.tryGetLocation(context)
            val exif = context
                .contentResolver
                .openFileDescriptor(newUri, "r")
                ?.use { ExifInterface(it.fileDescriptor) }

            getFileDescriptorFor(fileUri)?.use {
                val ex = ExifInterface(it.fileDescriptor)
                exif?.copyTo(ex)
                ex.saveAttributes()
            }
        } else Unit
    }

    private suspend infix fun ExifInterface.copyTo(
        newExif: ExifInterface
    ) = withContext(defaultDispatcher) {
        MetadataTag.entries.forEach { attr ->
            getAttribute(attr.key)?.let { newExif.setAttribute(attr.key, it) }
        }
        newExif.saveAttributes()
    }

    private data class SavingFolder(
        val outputStream: OutputStream? = null,
        val fileUri: Uri? = null
    )

    override fun constructImageFilename(
        saveTarget: ImageSaveTarget<*>,
        forceNotAddSizeInFilename: Boolean
    ): String {
        val extension = saveTarget.imageInfo.imageFormat.extension

        if (settingsState.randomizeFilename) return "${randomStringGenerator.generate(32)}.$extension"

        val wh =
            "(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(R.string.width)
                .split(" ")[0] else saveTarget.imageInfo.width) + ")x(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(
                R.string.height
            ).split(" ")[0] else saveTarget.imageInfo.height) + ")"

        var prefix = settingsState.filenamePrefix
        var suffix = settingsState.filenameSuffix

        if (prefix.isNotEmpty()) prefix = "${prefix}_"
        if (suffix.isNotEmpty()) suffix = "_$suffix"

        if (settingsState.addOriginalFilename) {
            prefix += if (saveTarget.originalUri.toUri() != Uri.EMPTY) {
                saveTarget.originalUri.toUri()
                    .getFilename()
                    ?.dropLastWhile { it != '.' }
                    ?.removeSuffix(".") ?: ""
            } else {
                context.getString(R.string.original_filename)
            }
        }
        if (settingsState.addSizeInFilename && !forceNotAddSizeInFilename) prefix += wh

        val timeStamp = if (settingsState.useFormattedFilenameTimestamp) {
            SimpleDateFormat(
                "yyyy-MM-dd_HH-mm-ss",
                Locale.getDefault()
            ).format(Date()) + "_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        } else Date().time.toString()

        val body = if (settingsState.addSequenceNumber && saveTarget.sequenceNumber != null) {
            if (settingsState.addOriginalFilename) {
                saveTarget.sequenceNumber.toString()
            } else {
                val timeStampPart = if (settingsState.addTimestampToFilename) {
                    timeStamp.dropLastWhile { it != '_' }
                } else ""

                timeStampPart + saveTarget.sequenceNumber
            }
        } else if (settingsState.addTimestampToFilename) {
            timeStamp
        } else ""

        if (body.isEmpty()) {
            if (prefix.endsWith("_")) prefix = prefix.dropLast(1)
            if (suffix.startsWith("_")) suffix = suffix.drop(1)
        }

        return "$prefix$body$suffix.$extension"
    }

    override fun constructImageFilename(
        saveTarget: ImageSaveTarget<*>,
        extension: String,
        forceNotAddSizeInFilename: Boolean
    ): String {
        if (settingsState.randomizeFilename) return "${randomStringGenerator.generate(32)}.$extension"

        val wh =
            "(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(R.string.width)
                .split(" ")[0] else saveTarget.imageInfo.width) + ")x(" + (if (saveTarget.originalUri.toUri() == Uri.EMPTY) context.getString(
                R.string.height
            ).split(" ")[0] else saveTarget.imageInfo.height) + ")"

        var prefix = settingsState.filenamePrefix
        var suffix = settingsState.filenameSuffix

        if (prefix.isNotEmpty()) prefix = "${prefix}_"
        if (suffix.isNotEmpty()) suffix = "_$suffix"

        if (settingsState.addOriginalFilename) {
            prefix += if (saveTarget.originalUri.toUri() != Uri.EMPTY) {
                saveTarget.originalUri.toUri()
                    .getFilename()
                    ?.dropLastWhile { it != '.' }
                    ?.removeSuffix(".") ?: ""
            } else {
                context.getString(R.string.original_filename)
            }
        }
        if (settingsState.addSizeInFilename && !forceNotAddSizeInFilename) prefix += wh

        val timeStamp = if (settingsState.useFormattedFilenameTimestamp) {
            SimpleDateFormat(
                "yyyy-MM-dd_HH-mm-ss",
                Locale.getDefault()
            ).format(Date()) + "_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        } else Date().time.toString()

        val body = if (settingsState.addSequenceNumber && saveTarget.sequenceNumber != null) {
            if (settingsState.addOriginalFilename) {
                saveTarget.sequenceNumber.toString()
            } else {
                val timeStampPart = if (settingsState.addTimestampToFilename) {
                    timeStamp.dropLastWhile { it != '_' }
                } else ""

                timeStampPart + saveTarget.sequenceNumber
            }
        } else if (settingsState.addTimestampToFilename) {
            timeStamp
        } else ""

        if (body.isEmpty()) {
            if (prefix.endsWith("_")) prefix = prefix.dropLast(1)
            if (suffix.startsWith("_")) suffix = suffix.drop(1)
        }

        return "$prefix$body$suffix.$extension"
    }

    override fun clearCache(onComplete: (String) -> Unit) = context.clearCache(onComplete)

    override fun getReadableCacheSize(): String = context.cacheSize()

    override suspend fun readBytes(
        uri: String
    ): ByteArray = withContext(ioDispatcher) {
        context.contentResolver.openInputStream(uri.toUri())?.use {
            it.buffered().readBytes()
        } ?: ByteArray(0)
    }

    override suspend fun writeBytes(
        uri: String,
        block: suspend (Writeable) -> Unit,
    ): SaveResult {
        runCatching {
            context.openWriteableStream(
                uri = uri.toUri(),
                onError = { throw it }
            )?.let { stream ->
                StreamWriteable(stream).use { block(it) }
            }
        }.onSuccess {
            return SaveResult.Success(null, "")
        }.onFailure {
            return SaveResult.Error.Exception(it)
        }

        return SaveResult.Error.Exception(IllegalStateException())
    }

    private fun Context.openWriteableStream(
        uri: Uri?,
        onError: (Throwable) -> Unit = {}
    ): OutputStream? = uri?.let {
        runCatching {
            contentResolver.openOutputStream(uri, "rw")
        }.getOrElse {
            runCatching {
                contentResolver.openOutputStream(uri, "w")
            }.onFailure(onError).getOrNull()
        }
    }

    private fun Context.clearCache(onComplete: (cache: String) -> Unit = {}) {
        CoroutineScope(defaultDispatcher).launch {
            coroutineScope {
                runCatching {
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

    private fun Context.cacheSize(): String = runCatching {
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

    private fun getFileDescriptorFor(
        uri: Uri?
    ): ParcelFileDescriptor? = uri?.let {
        runCatching {
            context.contentResolver.openFileDescriptor(uri, "rw")
        }.getOrNull()
    }

    private suspend fun getSavingFolder(
        treeUri: Uri?,
        saveTarget: SaveTarget
    ): SavingFolder = withContext(defaultDispatcher) {
        if (treeUri == null || DocumentFile.isDocumentUri(context, treeUri)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val type = saveTarget.mimeType
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
                val imageUri = context.contentResolver.insert(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    contentValues
                )

                SavingFolder(
                    outputStream = imageUri?.let { context.contentResolver.openOutputStream(it) },
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
            val documentFile = if (DocumentFile.isDocumentUri(context, treeUri)) {
                DocumentFile.fromSingleUri(context, treeUri)
            } else DocumentFile.fromTreeUri(context, treeUri)

            if (documentFile?.exists() == false || documentFile == null) {
                throw NoSuchFileException(File(treeUri.toString()))
            }

            val fileUri = try {
                documentFile.createFile(saveTarget.mimeType, saveTarget.filename!!)!!.uri
            } catch (_: UnsupportedOperationException) {
                documentFile.uri
            }

            SavingFolder(
                outputStream = context.contentResolver.openOutputStream(fileUri),
                fileUri = fileUri
            )
        }
    }

    private fun Uri.tryGetLocation(context: Context): Uri {
        val tempUri = this
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runCatching {
                MediaStore.setRequireOriginal(this).also {
                    context.contentResolver.openFileDescriptor(it, "r")?.close()
                }
            }.getOrNull() ?: tempUri
        } else this
    }

    private fun Uri.getFilename(): String? = DocumentFile.fromSingleUri(context, this)?.name

}