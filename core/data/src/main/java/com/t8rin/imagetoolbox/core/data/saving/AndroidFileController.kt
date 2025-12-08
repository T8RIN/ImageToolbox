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

package com.t8rin.imagetoolbox.core.data.saving

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.data.image.toMetadata
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.data.utils.cacheSize
import com.t8rin.imagetoolbox.core.data.utils.clearCache
import com.t8rin.imagetoolbox.core.data.utils.copyMetadata
import com.t8rin.imagetoolbox.core.data.utils.fileSize
import com.t8rin.imagetoolbox.core.data.utils.getFilename
import com.t8rin.imagetoolbox.core.data.utils.getPath
import com.t8rin.imagetoolbox.core.data.utils.isExternalStorageWritable
import com.t8rin.imagetoolbox.core.data.utils.listFilesInDirectory
import com.t8rin.imagetoolbox.core.data.utils.listFilesInDirectoryProgressive
import com.t8rin.imagetoolbox.core.data.utils.openFileDescriptor
import com.t8rin.imagetoolbox.core.data.utils.openWriteableStream
import com.t8rin.imagetoolbox.core.data.utils.toUiPath
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
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
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.use
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.reflect.KClass


internal class AndroidFileController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val shareProvider: ShareProvider,
    private val filenameCreator: FilenameCreator,
    private val jsonParser: JsonParser,
    private val appScope: AppScope,
    dispatchersHolder: DispatchersHolder,
    resourceManager: ResourceManager,
) : DispatchersHolder by dispatchersHolder,
    ResourceManager by resourceManager,
    FileController {

    private val _settingsState = settingsManager.settingsState
    private val settingsState get() = _settingsState.value

    override fun getSize(uri: String): Long? = uri.toUri().fileSize(context)

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
                    savingPath = savingPath
                )
            }

            val originalUri = saveTarget.originalUri.toUri()

            if (saveTarget is ImageSaveTarget && saveTarget.canSkipIfLarger && settingsState.allowSkipIfLarger) {
                val originalSize = originalUri.fileSize(context)
                val newSize = data.size

                if (originalSize != null && newSize > originalSize) {
                    return@withContext SaveResult.Skipped
                }
            }

            if (settingsState.overwriteFiles) {
                runCatching {
                    if (originalUri == Uri.EMPTY) throw IllegalStateException()

                    context.openFileDescriptor(
                        uri = originalUri,
                        mode = FileMode.WriteTruncate
                    )
                }.onFailure {
                    settingsManager.setImagePickerMode(3)
                    return@withContext SaveResult.Error.Exception(
                        Exception(
                            getString(
                                R.string.overwrite_file_requirements
                            )
                        )
                    )
                }.getOrNull()?.use { parcel ->
                    FileOutputStream(parcel.fileDescriptor).use { out ->
                        out.write(data)
                        context.copyMetadata(
                            initialExif = (saveTarget as? ImageSaveTarget)?.metadata,
                            fileUri = originalUri,
                            keepOriginalMetadata = keepOriginalMetadata,
                            originalUri = originalUri
                        )
                    }

                    return@withContext SaveResult.Success(
                        message = getString(
                            R.string.saved_to_original,
                            originalUri.getFilename(context).toString()
                        ),
                        isOverwritten = true,
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
                                getString(
                                    R.string.no_such_directory,
                                    treeUri.toUri().toUiPath(context, treeUri)
                                )
                            )
                        )
                    }
                } else {
                    documentFile = null
                }

                var initialExif: Metadata? = null

                val newSaveTarget = if (saveTarget is ImageSaveTarget) {
                    initialExif = saveTarget.metadata

                    saveTarget.copy(
                        filename = filenameCreator.constructImageFilename(
                            saveTarget = saveTarget,
                            forceNotAddSizeInFilename = saveTarget.imageInfo.height <= 0 || saveTarget.imageInfo.width <= 0
                        )
                    )
                } else saveTarget

                val savingFolder = SavingFolder.getInstance(
                    context = context,
                    treeUri = treeUri?.toUri(),
                    saveTarget = newSaveTarget
                ) ?: throw IllegalArgumentException(getString(R.string.error_while_saving))

                savingFolder.use {
                    it.writeBytes(data)
                }

                context.copyMetadata(
                    initialExif = initialExif,
                    fileUri = savingFolder.fileUri,
                    keepOriginalMetadata = keepOriginalMetadata,
                    originalUri = saveTarget.originalUri.toUri()
                )

                val filename = newSaveTarget.filename
                    ?: throw IllegalArgumentException(getString(R.string.filename_is_not_set))

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
                        val isFile =
                            (documentFile?.isDirectory != true && oneTimeSaveLocationUri != null)
                        if (isFile) {
                            getString(R.string.saved_to_custom)
                        } else if (filename.isNotEmpty()) {
                            getString(
                                R.string.saved_to,
                                savingPath,
                                filename
                            )
                        } else {
                            getString(
                                R.string.saved_to_without_filename,
                                savingPath
                            )
                        }
                    } else null,
                    savingPath = savingPath
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
        onComplete: (String) -> Unit,
    ) {
        appScope.launch {
            context.clearCache()
            onComplete(getReadableCacheSize())
        }
    }

    override fun getReadableCacheSize(): String = context.cacheSize()

    override suspend fun readBytes(
        uri: String,
    ): ByteArray = withContext(ioDispatcher) {
        runSuspendCatching {
            context.contentResolver.openInputStream(uri.toUri())?.use {
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
            context.openWriteableStream(
                uri = uri.toUri(),
                onFailure = {
                    uri.makeLog("File Controller write")
                    it.makeLog("File Controller write")
                    throw it
                }
            )?.let { stream ->
                StreamWriteable(stream).use { block(it) }
            }
        }.onSuccess {
            return@withContext SaveResult.Success(
                message = null,
                savingPath = ""
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
    ): SaveResult = writeBytes(
        uri = toUri,
        block = { output ->
            context.contentResolver.openInputStream(fromUri.toUri())?.buffered()?.use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (input.read(buffer) != -1) {
                    output.writeBytes(buffer)
                }
            } ?: throw IllegalAccessException("File inaccessible")
        }
    )

    override suspend fun transferBytes(
        fromUri: String,
        to: Writeable
    ): SaveResult = withContext(ioDispatcher) {
        runSuspendCatching {
            context.contentResolver.openInputStream(fromUri.toUri())?.buffered()?.use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (input.read(buffer) != -1) {
                    to.writeBytes(buffer)
                }
            } ?: throw IllegalAccessException("File inaccessible")
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
        "saveObject".makeLog(key)
        val json = jsonParser.toJson(value, value::class.java) ?: return@withContext false
        val file = File(context.filesDir, "$key.json")

        runCatching {
            file.outputStream().use {
                it.write(json.toByteArray(Charsets.UTF_8))
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
            val file = File(context.filesDir, "$key.json").apply {
                if (!exists()) createNewFile()
            }

            jsonParser.fromJson<O>(file.readText(Charsets.UTF_8), kClass.java)
        }.onFailure {
            it.makeLog("restoreObject $key")
        }.onSuccess {
            "restoreObject success".makeLog(key)
        }.getOrNull()
    }

    override suspend fun writeMetadata(
        imageUri: String,
        metadata: Metadata?
    ) {
        context.copyMetadata(
            initialExif = metadata,
            fileUri = imageUri.toUri(),
            keepOriginalMetadata = false,
            originalUri = imageUri.toUri()
        )
    }

    override suspend fun readMetadata(
        imageUri: String
    ): Metadata? = context.openFileDescriptor(imageUri.toUri())?.fileDescriptor?.toMetadata()

    override suspend fun listFilesInDirectory(
        treeUri: String
    ): List<String> = withContext(ioDispatcher) {
        context.listFilesInDirectory(treeUri.toUri()).map { it.toString() }
    }

    override fun listFilesInDirectoryAsFlow(
        treeUri: String
    ): Flow<String> = context.listFilesInDirectoryProgressive(treeUri.toUri()).map(Uri::toString)

}