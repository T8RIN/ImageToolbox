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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import okio.use
import ru.tech.imageresizershrinker.core.data.utils.cacheSize
import ru.tech.imageresizershrinker.core.data.utils.clearCache
import ru.tech.imageresizershrinker.core.data.utils.copyMetadata
import ru.tech.imageresizershrinker.core.data.utils.fileSize
import ru.tech.imageresizershrinker.core.data.utils.getFilename
import ru.tech.imageresizershrinker.core.data.utils.getPath
import ru.tech.imageresizershrinker.core.data.utils.isExternalStorageWritable
import ru.tech.imageresizershrinker.core.data.utils.openWriteableStream
import ru.tech.imageresizershrinker.core.data.utils.toUiPath
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.json.JsonParser
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.FilenameCreator
import ru.tech.imageresizershrinker.core.domain.saving.Writeable
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.use
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.OneTimeSaveLocation
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.reflect.KClass


internal class AndroidFileController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val shareProvider: ShareProvider<Bitmap>,
    private val filenameCreator: FilenameCreator,
    private val jsonParser: JsonParser,
    dispatchersHolder: DispatchersHolder,
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

    override fun getSize(uri: String): Long? = uri.toUri().fileSize(context)

    override val defaultSavingPath: String
        get() = settingsState.saveFolderUri.getPath(context)

    override suspend fun save(
        saveTarget: SaveTarget,
        keepOriginalMetadata: Boolean,
        oneTimeSaveLocationUri: String?,
    ): SaveResult = withContext(ioDispatcher) {
        if (!context.isExternalStorageWritable()) {
            return@withContext SaveResult.Error.MissingPermissions
        }

        val data = if (saveTarget is ImageSaveTarget<*> && saveTarget.readFromUriInsteadOfData) {
            readBytes(saveTarget.originalUri)
        } else saveTarget.data

        val savingPath = oneTimeSaveLocationUri?.getPath(context) ?: defaultSavingPath

        runCatching {
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
                    message = context.getString(R.string.copied),
                    savingPath = savingPath
                )
            }

            val originalUri = saveTarget.originalUri.toUri()

            if (settingsState.overwriteFiles) {
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
                        out.write(data)
                        context.copyMetadata(
                            initialExif = (saveTarget as? ImageSaveTarget<*>)?.metadata as ExifInterface?,
                            fileUri = originalUri,
                            keepMetadata = keepOriginalMetadata,
                            originalUri = originalUri
                        )
                    }

                    return@withContext SaveResult.Success(
                        message = context.getString(
                            R.string.saved_to_original,
                            originalUri.getFilename(context).toString()
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
                )

                savingFolder.outputStream?.use {
                    it.write(data)
                }

                context.copyMetadata(
                    initialExif = initialExif,
                    fileUri = savingFolder.fileUri,
                    keepMetadata = keepOriginalMetadata,
                    originalUri = saveTarget.originalUri.toUri()
                )

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
                        val isFile =
                            (documentFile?.isDirectory != true && oneTimeSaveLocationUri != null)
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
            return@withContext SaveResult.Error.Exception(it)
        }

        SaveResult.Error.Exception(
            SaveException(
                message = context.getString(R.string.something_went_wrong)
            )
        )
    }

    override fun clearCache(
        onComplete: (String) -> Unit,
    ) = context.clearCache(
        dispatcher = ioDispatcher,
        onComplete = onComplete
    )

    override fun getReadableCacheSize(): String = context.cacheSize()

    override suspend fun readBytes(
        uri: String,
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

    override suspend fun <O : Any> saveObject(
        key: String,
        value: O,
    ): Boolean = withContext(ioDispatcher) {
        val json = jsonParser.toJson(value, value::class.java) ?: return@withContext false
        val file = File(context.filesDir, "$key.json")

        runCatching {
            file.outputStream().use {
                it.write(json.toByteArray(Charsets.UTF_8))
            }
        }.onSuccess {
            return@withContext true
        }.onFailure {
            return@withContext false
        }

        return@withContext false
    }

    override suspend fun <O : Any> restoreObject(
        key: String,
        kClass: KClass<O>,
    ): O? = withContext(ioDispatcher) {
        runCatching {
            val file = File(context.filesDir, "$key.json").apply {
                if (!exists()) createNewFile()
            }

            jsonParser.fromJson<O>(file.readText(Charsets.UTF_8), kClass.java)
        }.getOrNull()
    }

    override suspend fun <M> writeMetadata(
        imageUri: String,
        metadata: M?
    ) {
        if (metadata is ExifInterface?) {
            context.copyMetadata(
                initialExif = metadata,
                fileUri = imageUri.toUri(),
                keepMetadata = false,
                originalUri = imageUri.toUri()
            )
        }
    }

}