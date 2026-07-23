/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.data.saving

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.documentfile.provider.DocumentFile
import coil3.ImageLoader
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.data.coil.remove
import com.t8rin.imagetoolbox.core.data.image.toMetadata
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.saving.io.UriWriteable
import com.t8rin.imagetoolbox.core.data.utils.cacheSize
import com.t8rin.imagetoolbox.core.data.utils.clearCache
import com.t8rin.imagetoolbox.core.data.utils.computeFromReadable
import com.t8rin.imagetoolbox.core.data.utils.isExternalStorageWritable
import com.t8rin.imagetoolbox.core.data.utils.openFileDescriptor
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.history.AppHistoryRepository
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.copyTo
import com.t8rin.imagetoolbox.core.domain.image.get
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.readOnly
import com.t8rin.imagetoolbox.core.domain.image.set
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.domain.saving.io.use
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.FileMode
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.utils.UriReplacements
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.getPath
import com.t8rin.imagetoolbox.core.utils.listFilesInDirectory
import com.t8rin.imagetoolbox.core.utils.listFilesInDirectoryProgressive
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.core.utils.tryExtractOriginal
import com.t8rin.imagetoolbox.core.utils.uiPath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.reflect.KClass


internal class AndroidFileController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val shareProvider: ShareProvider,
    private val filenameCreator: FilenameCreator,
    private val jsonParser: JsonParser,
    private val appHistoryRepository: AppHistoryRepository,
    private val appScope: AppScope,
    private val originalFileDeletionHelper: OriginalFileDeletionHelper,
    private val dataStore: DataStore<Preferences>,
    private val imageLoader: ImageLoader,
    dispatchersHolder: DispatchersHolder,
    resourceManager: ResourceManager,
) : DispatchersHolder by dispatchersHolder,
    ResourceManager by resourceManager,
    FileController {

    private val _settingsState = settingsManager.settingsState
    private val settingsState get() = _settingsState.value

    override fun getSize(uri: String): Long? = uri.toUri().fileSize()

    override val defaultSavingPath: String
        get() = settingsState.saveFolderUri.getPath(context)

    override suspend fun save(
        saveTarget: SaveTarget,
        keepOriginalMetadata: Boolean,
        oneTimeSaveLocationUri: String?,
    ): SaveResult {
        val result = saveImpl(
            saveTarget = saveTarget,
            keepOriginalMetadata = keepOriginalMetadata,
            oneTimeSaveLocationUri = oneTimeSaveLocationUri
        )

        Triple(
            first = result,
            second = keepOriginalMetadata,
            third = oneTimeSaveLocationUri
        ).makeLog("File Controller save")

        if (result is SaveResult.Success) {
            appHistoryRepository.registerSuccessfulSave(
                savedBytes = result.savedBytes,
                savedFormat = if (saveTarget is ImageSaveTarget) {
                    saveTarget.imageFormat.title
                } else {
                    saveTarget.extension
                }
            )
            originalFileDeletionHelper.onSuccessfulSave()
        }

        return result
    }

    private suspend fun saveImpl(
        saveTarget: SaveTarget,
        keepOriginalMetadata: Boolean,
        oneTimeSaveLocationUri: String?,
    ): SaveResult = withContext(ioDispatcher) {
        if (!context.isExternalStorageWritable()) {
            return@withContext SaveResult.Error.MissingPermissions
        }

        val shouldKeepMetadata = keepOriginalMetadata && !settingsState.isAlwaysClearExif
        val data = if (saveTarget is ImageSaveTarget && saveTarget.readFromUriInsteadOfData) {
            readBytes(saveTarget.originalUri)
        } else {
            saveTarget.data
        }

        val savingPath = oneTimeSaveLocationUri?.getPath(context) ?: defaultSavingPath

        runSuspendCatching {
            if (settingsState.copyToClipboardMode is CopyToClipboardMode.Enabled) {
                val clipboardManager = context.getSystemService<ClipboardManager>()

                shareProvider.cacheByteArray(
                    byteArray = data,
                    filename = filenameCreator.constructRandomFilename(saveTarget.extension)
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
                    message = getString(R.string.copied),
                    savingPath = savingPath,
                    savedBytes = data.size.toLong()
                )
            }

            val originalUri = UriReplacements.resolve(saveTarget.originalUri.toUri())

            if (saveTarget is ImageSaveTarget && saveTarget.canSkipIfLarger && settingsState.allowSkipIfLarger) {
                val originalSize = originalUri.fileSize()
                val newSize = data.size

                if (originalSize != null && newSize > originalSize) {
                    return@withContext SaveResult.Skipped(saveTarget.originalUri)
                }
            }

            if (settingsState.filenameBehavior is FilenameBehavior.Overwrite) {
                val providedMetadata = (saveTarget as? ImageSaveTarget)
                    ?.metadata
                    ?.takeUnless { settingsState.isAlwaysClearExif }
                val targetMetadata = if (shouldKeepMetadata) {
                    readMetadata(originalUri.toString()) ?: providedMetadata
                } else {
                    providedMetadata
                }

                val parcel = runCatching {
                    if (originalUri == Uri.EMPTY) throw IllegalStateException()

                    context.openFileDescriptor(
                        uri = originalUri,
                        mode = FileMode.WriteTruncate
                    )
                }.getOrNull()

                if (parcel == null) {
                    settingsManager.setImagePickerMode(FILE_EXPLORER_PICKER_MODE)
                    return@withContext SaveResult.Error.Exception(
                        Throwable(getString(R.string.overwrite_file_requirements))
                    )
                }

                parcel.use {
                    FileOutputStream(parcel.fileDescriptor).use { out ->
                        out.write(data)

                        copyMetadata(
                            initialExif = targetMetadata,
                            fileUri = originalUri,
                            keepOriginalMetadata = shouldKeepMetadata,
                            originalUri = originalUri,
                            imageSaveTarget = saveTarget as? ImageSaveTarget
                        )
                    }

                    imageLoader.apply {
                        memoryCache?.remove(originalUri.toString())
                        diskCache?.remove(originalUri.toString())
                    }

                    return@withContext SaveResult.Success(
                        message = getString(
                            R.string.saved_to_original,
                            originalUri.filename(context).toString()
                        ),
                        isOverwritten = true,
                        savingPath = savingPath,
                        savedBytes = data.size.toLong()
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

                    if (documentFile == null || !documentFile.exists()) {
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
                            Throwable(
                                getString(
                                    R.string.no_such_directory,
                                    treeUri.toUri().uiPath(treeUri, context)
                                )
                            )
                        )
                    }
                } else {
                    documentFile = null
                }

                var initialExif: Metadata? = null

                val newSaveTarget = if (saveTarget is ImageSaveTarget) {
                    initialExif = saveTarget.metadata.takeUnless { settingsState.isAlwaysClearExif }

                    saveTarget.copy(
                        filename = filenameCreator.constructImageFilename(
                            saveTarget = saveTarget,
                            forceNotAddSizeInFilename = saveTarget.imageInfo.height <= 0 || saveTarget.imageInfo.width <= 0
                        )
                    )
                } else saveTarget

                val filename = newSaveTarget.filename
                    ?: throw IllegalArgumentException(getString(R.string.filename_is_not_set))

                val savingFolder = SavingFolder.getInstance(
                    context = context,
                    treeUri = treeUri?.toUri(),
                    saveTarget = newSaveTarget,
                    saveToOriginalFolder = settingsState.saveToOriginalFolder
                ) ?: throw IllegalArgumentException(getString(R.string.error_while_saving))

                savingFolder.use {
                    it.writeBytes(data)
                }

                copyMetadata(
                    initialExif = initialExif,
                    fileUri = savingFolder.fileUri,
                    keepOriginalMetadata = shouldKeepMetadata,
                    originalUri = saveTarget.originalUri.toUri(),
                    imageSaveTarget = saveTarget as? ImageSaveTarget
                )

                if (settingsState.deleteOriginalsAfterSave && settingsState.filenameBehavior !is FilenameBehavior.Overwrite) {
                    originalFileDeletionHelper.deleteAfterSuccessfulSave(
                        originalUri = saveTarget.originalUri,
                        outputUri = savingFolder.fileUri
                    )
                }

                savingFolder.fileUri.path?.takeIf {
                    savingFolder.fileUri.scheme == "file"
                }?.let { path ->
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(path),
                        arrayOf(newSaveTarget.mimeType.entry),
                        null
                    )
                }

                val actualSavingPath = savingFolder.normalizedSavingPath ?: savingPath

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
                    message = if (actualSavingPath.isNotEmpty()) {
                        val isFile =
                            (documentFile?.isDirectory != true && oneTimeSaveLocationUri != null)
                        if (isFile) {
                            getString(R.string.saved_to_custom)
                        } else if (filename.isNotEmpty()) {
                            getString(
                                R.string.saved_to,
                                actualSavingPath,
                                filename
                            )
                        } else {
                            getString(
                                R.string.saved_to_without_filename,
                                actualSavingPath
                            )
                        }
                    } else null,
                    savingPath = actualSavingPath,
                    savedBytes = data.size.toLong()
                )
            }
        }.onFailure {
            return@withContext SaveResult.Error.Exception(it)
        }

        SaveResult.Error.Exception(
            SaveException(
                message = getString(R.string.something_went_wrong)
            )
        )
    }

    override suspend fun move(
        sourceUri: String,
        saveTarget: SaveTarget,
        keepOriginalMetadata: Boolean,
        oneTimeSaveLocationUri: String?,
    ): SaveResult {
        val result = moveImpl(
            sourceUri = sourceUri,
            saveTarget = saveTarget,
            keepOriginalMetadata = keepOriginalMetadata,
            oneTimeSaveLocationUri = oneTimeSaveLocationUri
        )

        Triple(
            first = result,
            second = keepOriginalMetadata,
            third = oneTimeSaveLocationUri
        ).makeLog("File Controller move")

        if (result is SaveResult.Success) {
            appHistoryRepository.registerSuccessfulSave(
                savedBytes = result.savedBytes,
                savedFormat = if (saveTarget is ImageSaveTarget) {
                    saveTarget.imageFormat.title
                } else {
                    saveTarget.extension
                }
            )

            runCatching {
                context.contentResolver.delete(
                    UriReplacements.resolve(sourceUri.toUri()),
                    null,
                    null
                )
            }.onFailure {
                it.makeLog("File Controller move source cleanup")
            }
        }

        return result
    }

    private suspend fun moveImpl(
        sourceUri: String,
        saveTarget: SaveTarget,
        keepOriginalMetadata: Boolean,
        oneTimeSaveLocationUri: String?,
    ): SaveResult = withContext(ioDispatcher) {
        if (!context.isExternalStorageWritable()) {
            return@withContext SaveResult.Error.MissingPermissions
        }

        val shouldKeepMetadata = keepOriginalMetadata && !settingsState.isAlwaysClearExif
        val resolvedSourceUri = UriReplacements.resolve(sourceUri.toUri())
        val sourceSize = resolvedSourceUri.fileSize() ?: 0L
        val savingPath = oneTimeSaveLocationUri?.getPath(context) ?: defaultSavingPath

        fun copySourceTo(writeable: Writeable) {
            UriReadable(
                uri = resolvedSourceUri,
                context = context
            ).use {
                it.copyTo(writeable)
            }
        }

        runSuspendCatching {
            if (settingsState.copyToClipboardMode is CopyToClipboardMode.Enabled) {
                val clipboardManager = context.getSystemService<ClipboardManager>()

                shareProvider.cacheData(
                    filename = filenameCreator.constructRandomFilename(saveTarget.extension),
                    writeData = ::copySourceTo
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
                    message = getString(R.string.copied),
                    savingPath = savingPath,
                    savedBytes = sourceSize
                )
            }

            val originalUri = UriReplacements.resolve(saveTarget.originalUri.toUri())

            if (saveTarget is ImageSaveTarget && saveTarget.canSkipIfLarger && settingsState.allowSkipIfLarger) {
                val originalSize = originalUri.fileSize()

                if (originalSize != null && sourceSize > originalSize) {
                    return@withContext SaveResult.Skipped(saveTarget.originalUri)
                }
            }

            if (settingsState.filenameBehavior is FilenameBehavior.Overwrite) {
                if (resolvedSourceUri == originalUri) {
                    throw IllegalArgumentException("Source and destination URI must be different")
                }

                val providedMetadata = (saveTarget as? ImageSaveTarget)
                    ?.metadata
                    ?.takeUnless { settingsState.isAlwaysClearExif }
                val targetMetadata = if (shouldKeepMetadata) {
                    readMetadata(originalUri.toString()) ?: providedMetadata
                } else {
                    providedMetadata
                }

                val parcel = runCatching {
                    if (originalUri == Uri.EMPTY) throw IllegalStateException()

                    context.openFileDescriptor(
                        uri = originalUri,
                        mode = FileMode.WriteTruncate
                    )
                }.getOrNull()

                if (parcel == null) {
                    settingsManager.setImagePickerMode(FILE_EXPLORER_PICKER_MODE)
                    return@withContext SaveResult.Error.Exception(
                        Throwable(getString(R.string.overwrite_file_requirements))
                    )
                }

                parcel.use {
                    FileOutputStream(parcel.fileDescriptor).use { out ->
                        copySourceTo(StreamWriteable(out))

                        copyMetadata(
                            initialExif = targetMetadata,
                            fileUri = originalUri,
                            keepOriginalMetadata = shouldKeepMetadata,
                            originalUri = originalUri,
                            imageSaveTarget = saveTarget as? ImageSaveTarget
                        )
                    }

                    imageLoader.apply {
                        memoryCache?.remove(originalUri.toString())
                        diskCache?.remove(originalUri.toString())
                    }

                    return@withContext SaveResult.Success(
                        message = getString(
                            R.string.saved_to_original,
                            originalUri.filename(context).toString()
                        ),
                        isOverwritten = true,
                        savingPath = savingPath,
                        savedBytes = originalUri.fileSize() ?: sourceSize
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

                    if (documentFile == null || !documentFile.exists()) {
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
                            Throwable(
                                getString(
                                    R.string.no_such_directory,
                                    treeUri.toUri().uiPath(treeUri, context)
                                )
                            )
                        )
                    }
                } else {
                    documentFile = null
                }

                var initialExif: Metadata? = null

                val newSaveTarget = if (saveTarget is ImageSaveTarget) {
                    initialExif = saveTarget.metadata.takeUnless { settingsState.isAlwaysClearExif }

                    saveTarget.copy(
                        filename = when (val behavior = settingsState.filenameBehavior) {
                            is FilenameBehavior.Checksum -> UriReadable(
                                uri = resolvedSourceUri,
                                context = context
                            ).use {
                                "${behavior.hashingType.computeFromReadable(it)}.${saveTarget.extension}"
                            }

                            else -> filenameCreator.constructImageFilename(
                                saveTarget = saveTarget,
                                forceNotAddSizeInFilename = saveTarget.imageInfo.height <= 0 || saveTarget.imageInfo.width <= 0
                            )
                        }
                    )
                } else saveTarget

                val filename = newSaveTarget.filename
                    ?: throw IllegalArgumentException(getString(R.string.filename_is_not_set))

                val savingFolder = SavingFolder.getInstance(
                    context = context,
                    treeUri = treeUri?.toUri(),
                    saveTarget = newSaveTarget,
                    saveToOriginalFolder = settingsState.saveToOriginalFolder
                ) ?: throw IllegalArgumentException(getString(R.string.error_while_saving))

                savingFolder.use {
                    copySourceTo(it)
                }

                copyMetadata(
                    initialExif = initialExif,
                    fileUri = savingFolder.fileUri,
                    keepOriginalMetadata = shouldKeepMetadata,
                    originalUri = saveTarget.originalUri.toUri(),
                    imageSaveTarget = saveTarget as? ImageSaveTarget
                )

                if (settingsState.deleteOriginalsAfterSave && settingsState.filenameBehavior !is FilenameBehavior.Overwrite) {
                    originalFileDeletionHelper.deleteAfterSuccessfulSave(
                        originalUri = saveTarget.originalUri,
                        outputUri = savingFolder.fileUri
                    )
                }

                savingFolder.fileUri.path?.takeIf {
                    savingFolder.fileUri.scheme == "file"
                }?.let { path ->
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(path),
                        arrayOf(newSaveTarget.mimeType.entry),
                        null
                    )
                }

                val actualSavingPath = savingFolder.normalizedSavingPath ?: savingPath

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
                    message = if (actualSavingPath.isNotEmpty()) {
                        val isFile =
                            (documentFile?.isDirectory != true && oneTimeSaveLocationUri != null)
                        if (isFile) {
                            getString(R.string.saved_to_custom)
                        } else if (filename.isNotEmpty()) {
                            getString(
                                R.string.saved_to,
                                actualSavingPath,
                                filename
                            )
                        } else {
                            getString(
                                R.string.saved_to_without_filename,
                                actualSavingPath
                            )
                        }
                    } else null,
                    savingPath = actualSavingPath,
                    savedBytes = savingFolder.fileUri.fileSize() ?: sourceSize
                )
            }
        }.onFailure {
            return@withContext SaveResult.Error.Exception(it)
        }

        SaveResult.Error.Exception(
            SaveException(
                message = getString(R.string.something_went_wrong)
            )
        )
    }

    override fun clearCache(
        onComplete: (Long) -> Unit,
    ) {
        appScope.launch {
            context.clearCache()
            onComplete(getCacheSize())
            "cache cleared".makeLog("AndroidFileController")
        }
    }

    override fun getCacheSize(): Long = context.cacheSize()

    override suspend fun readBytes(
        uri: String,
    ): ByteArray = withContext(ioDispatcher) {
        runSuspendCatching {
            context.contentResolver.openInputStream(
                UriReplacements.resolve(uri.toUri())
            )?.use {
                it.buffered().readBytes()
            }
        }.onFailure {
            uri.makeLog("File Controller read")
            it.makeLog("File Controller read")
        }.getOrNull() ?: ByteArray(0)
    }

    override suspend fun writeBytes(
        uri: String,
        block: suspend (Writeable) -> Unit,
    ): SaveResult = withContext(ioDispatcher) {
        runSuspendCatching {
            block(
                UriWriteable(
                    uri = uri.toUri(),
                    context = context
                )
            )
        }.onSuccess {
            val savedBytes = uri.toUri().fileSize() ?: 0

            appHistoryRepository.registerSuccessfulSave(
                savedBytes = savedBytes,
                savedFormat = ""
            )
            return@withContext SaveResult.Success(
                message = null,
                savingPath = "",
                savedBytes = savedBytes
            )
        }.onFailure {
            uri.makeLog("File Controller write")
            it.makeLog("File Controller write")
            return@withContext SaveResult.Error.Exception(it)
        }

        return@withContext SaveResult.Error.Exception(IllegalStateException())
    }

    override suspend fun transferBytes(
        fromUri: String,
        toUri: String
    ): SaveResult = transferBytes(
        fromUri = fromUri,
        to = UriWriteable(
            uri = toUri.toUri(),
            context = context
        )
    )

    override suspend fun transferBytes(
        fromUri: String,
        to: Writeable
    ): SaveResult = withContext(ioDispatcher) {
        runSuspendCatching {
            UriReadable(
                uri = UriReplacements.resolve(fromUri.toUri()),
                context = context
            ).copyTo(to)
        }.onSuccess {
            return@withContext SaveResult.Success(
                message = null,
                savingPath = ""
            )
        }.onFailure {
            to.makeLog("File Controller write")
            it.makeLog("File Controller write")
            return@withContext SaveResult.Error.Exception(it)
        }

        return@withContext SaveResult.Error.Exception(IllegalStateException())
    }

    override suspend fun <O : Any> saveObject(
        key: String,
        value: O,
    ): Boolean = withContext(ioDispatcher) {
        "saveObject value = $value".makeLog(key)
        runCatching {
            dataStore.edit {
                it[stringPreferencesKey("fast_$key")] =
                    jsonParser.toJson(value, value::class.java)!!
            }
        }.onSuccess {
            "saveObject success".makeLog(key)
            return@withContext true
        }.onFailure {
            it.makeLog("saveObject $key")
            return@withContext false
        }

        return@withContext false
    }

    override suspend fun <O : Any> restoreObject(
        key: String,
        kClass: KClass<O>,
    ): O? = withContext(ioDispatcher) {
        runCatching {
            "restoreObject".makeLog(key)
            jsonParser.fromJson<O>(
                json = dataStore.data.first()[stringPreferencesKey("fast_$key")].orEmpty(),
                type = kClass.java
            )
        }.onFailure {
            it.makeLog("restoreObject $key")
        }.onSuccess {
            "restoreObject success value = $it".makeLog(key)
        }.getOrNull()
    }

    override suspend fun writeMetadata(
        imageUri: String,
        metadata: Metadata?
    ) {
        UriReplacements.resolve(imageUri.toUri()).let { resolvedUri ->
            copyMetadata(
                initialExif = metadata,
                fileUri = resolvedUri,
                keepOriginalMetadata = false,
                originalUri = resolvedUri
            )
        }
    }

    override suspend fun readMetadata(
        imageUri: String
    ): Metadata? = runSuspendCatching {
        context.contentResolver.openInputStream(
            UriReplacements.resolve(imageUri.toUri())
        )?.use {
            ExifInterface(it).toMetadata().readOnly().makeLog("readMetadata")
        }
    }.getOrNull()

    override suspend fun listFilesInDirectory(
        treeUri: String
    ): List<String> = withContext(ioDispatcher) {
        treeUri.toUri().listFilesInDirectory().map { it.toString() }
    }

    override fun listFilesInDirectoryAsFlow(
        treeUri: String
    ): Flow<String> = treeUri.toUri().listFilesInDirectoryProgressive().map {
        it.toString()
    }.flowOn(ioDispatcher)

    private suspend fun copyMetadata(
        initialExif: Metadata?,
        fileUri: Uri,
        keepOriginalMetadata: Boolean,
        originalUri: Uri,
        imageSaveTarget: ImageSaveTarget? = null
    ) = runSuspendCatching {
        if (initialExif != null) {
            openFileDescriptor(fileUri)?.use {
                initialExif.makeLog("initialMetadata")
                    .copyTo(it.fileDescriptor.toMetadata().makeLog("dstMetadata"))
            }
        } else if (keepOriginalMetadata) {
            if (fileUri != originalUri) {
                openFileDescriptor(fileUri)?.use {
                    readMetadata(originalUri.toString()).makeLog("srcMetadata")?.copyTo(
                        it.fileDescriptor.toMetadata().makeLog("dstMetadata")
                    )
                }
            } else {
                "Nothing, copying from self to self is pointless".makeLog("copyMetadata")
            }
        } else {
            openFileDescriptor(fileUri)?.use {
                it.fileDescriptor.toMetadata().apply {
                    clearAllAttributes()

                    if (settingsState.keepDateTime && !settingsState.isAlwaysClearExif) {
                        readMetadata(originalUri.toString()).makeLog("srcMetadata")
                            ?.copyTo(
                                metadata = this,
                                tags = MetadataTag.dateEntries
                            )
                    } else {
                        saveAttributes()
                    }
                }
            }.makeLog("metadataCleared")
        }

        addImageToolboxMetadata(
            fileUri = fileUri,
            imageSaveTarget = imageSaveTarget
        )
    }

    private fun addImageToolboxMetadata(
        fileUri: Uri,
        imageSaveTarget: ImageSaveTarget?
    ) {
        if (
            settingsState.addImageToolboxMetadata &&
            !settingsState.isAlwaysClearExif &&
            imageSaveTarget?.imageFormat?.canWriteExif == true
        ) {
            openFileDescriptor(fileUri)?.use {
                it.fileDescriptor.toMetadata().apply {
                    this[MetadataTag.Software] =
                        "ImageToolbox ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

                    val oldComment = this[MetadataTag.UserComment]
                        ?.lineSequence()
                        ?.filterNot { line -> line.startsWith(IMAGETOOLBOX_METADATA_PREFIX) }
                        ?.joinToString("\n")
                        ?.takeIf(String::isNotBlank)

                    this[MetadataTag.UserComment] = listOfNotNull(
                        oldComment,
                        imageSaveTarget.imageToolboxMetadata()
                    ).joinToString("\n")

                    saveAttributes()
                }
            }.makeLog("imageToolboxMetadataAdded")
        }
    }

    private fun openFileDescriptor(
        imageUri: Uri
    ) = context.openFileDescriptor(
        uri = imageUri.tryExtractOriginal(),
        mode = FileMode.ReadWrite
    )
}

private fun ImageSaveTarget.imageToolboxMetadata(): String {
    val info = imageInfo

    return buildList {
        add("format=${imageFormat.title}")
        add("size=${info.width}x${info.height}")
        add("quality=${info.quality}")
        add("resizeType=${info.resizeType}")
        add("rotationDegrees=${info.rotationDegrees}")
        add("isFlipped=${info.isFlipped}")
        add("imageScaleMode=${info.imageScaleMode.javaClass.simpleName}")
        add("scaleColorSpace=${info.imageScaleMode.scaleColorSpace}")
        presetInfo?.asString()?.takeIf(String::isNotEmpty)?.let {
            add("preset=$it")
        }
    }.joinToString(
        prefix = IMAGETOOLBOX_METADATA_PREFIX,
        separator = "; "
    )
}

private const val FILE_EXPLORER_PICKER_MODE = 3
private const val IMAGETOOLBOX_METADATA_PREFIX = "ImageToolbox parameters: "