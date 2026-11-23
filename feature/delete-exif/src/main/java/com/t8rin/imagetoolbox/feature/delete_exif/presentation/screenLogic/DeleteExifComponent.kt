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

package com.t8rin.imagetoolbox.feature.delete_exif.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.model.onSuccess
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.savable
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifLocation
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifRemovalPreset
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifReplacementPreset
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifReplacementSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

class DeleteExifComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val fileController: FileController,
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val shareProvider: ShareProvider,
    private val filenameCreator: FilenameCreator,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::updateUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving: Boolean by _isSaving

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap: Bitmap? by _previewBitmap

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _selectedUri: MutableState<Uri?> = mutableStateOf(null)
    val selectedUri by _selectedUri

    private val _selectedTags: MutableState<List<MetadataTag>> = mutableStateOf(emptyList())
    val selectedTags by _selectedTags

    private val _removalPreset = mutableStateOf(ExifRemovalPreset.AllMetadata)
    val removalPreset by _removalPreset

    private val _replaceExif = mutableStateOf(false)
    internal val replaceExif by _replaceExif

    private val _replacementPreset = mutableStateOf(ExifReplacementPreset.Custom)
    internal val replacementPreset by _replacementPreset

    private val _replacementSettings = mutableStateOf(ExifReplacementSettings())
    internal val replacementSettings by _replacementSettings

    private val _customTags = mutableStateOf<Map<MetadataTag, String>>(emptyMap())
    internal val customTags by _customTags

    private var savedTemplates by fileController.savable(
        scope = componentScope,
        initial = ExifTemplates(),
        key = "ExifTemplates"
    )
    internal val templates get() = savedTemplates.tagsByName.toList()
    internal val customTemplate get() = customTags.mapKeys { (tag, _) -> tag.key }
    internal val canSave
        get() = when {
            !replaceExif -> true
            replacementPreset == ExifReplacementPreset.Custom -> customTags.isNotEmpty()
            replacementSettings.location == ExifLocation.Manual -> replacementSettings.coordinates != null
            else -> true
        }

    internal val generatedTags
        get() = replacementPreset.generateTags(
            settings = replacementSettings,
            index = uris.orEmpty().indexOf(selectedUri).coerceAtLeast(0)
        )

    internal fun setReplaceExif(value: Boolean) {
        _replaceExif.value = value
    }

    internal fun setReplacementPreset(value: ExifReplacementPreset) {
        _replacementPreset.value = value
    }

    internal fun updateReplacementSettings(
        block: ExifReplacementSettings.() -> ExifReplacementSettings
    ) {
        _replacementSettings.update(block)
    }

    internal fun regenerateTags() = updateReplacementSettings {
        copy(seed = Random.nextLong(), now = LocalDateTime.now(ZoneOffset.UTC))
    }

    internal fun editGeneratedTags() {
        _customTags.value = generatedTags
        _replacementPreset.value = ExifReplacementPreset.Custom
    }

    internal fun updateCustomTag(tag: MetadataTag, value: String) {
        if (value.isBlank()) removeCustomTag(tag)
        else _customTags.update { it + (tag to value) }
    }

    internal fun removeCustomTag(tag: MetadataTag) {
        _customTags.update { it - tag }
    }

    internal fun clearCustomTags() {
        _customTags.value = emptyMap()
    }

    internal fun applyTemplate(template: Map<String, String>) {
        _customTags.value = MetadataTag.commonWritableEntries.mapNotNull { tag ->
            template[tag.key]?.takeIf(String::isNotBlank)?.let { tag to it }
        }.toMap()
    }

    internal fun saveTemplate(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty() || customTags.isEmpty()) return

        savedTemplates = savedTemplates.copy(
            tagsByName = mapOf(trimmedName to customTemplate) +
                    savedTemplates.tagsByName.filterKeys {
                        !it.equals(
                            trimmedName,
                            ignoreCase = true
                        )
                    }
        )
    }

    internal fun deleteTemplate(name: String) {
        savedTemplates = savedTemplates.copy(tagsByName = savedTemplates.tagsByName - name)
    }

    fun updateUris(
        uris: List<Uri>?
    ) {
        _uris.value = null
        _uris.value = uris
        uris?.firstOrNull()?.let(::updateSelectedUri)
    }

    fun updateUrisSilently(removedUri: Uri) {
        componentScope.launch {
            _uris.value = uris
            if (_selectedUri.value == removedUri) {
                val index = uris?.indexOf(removedUri) ?: -1
                if (index == 0) {
                    uris?.getOrNull(1)?.let(::updateSelectedUri)
                } else {
                    uris?.getOrNull(index - 1)?.let(::updateSelectedUri)
                }
            }
            val u = _uris.value?.toMutableList()?.apply {
                remove(removedUri)
            }
            _uris.value = u
        }
    }

    private fun updateBitmap(bitmap: Bitmap?) {
        componentScope.launch {
            _isImageLoading.value = true
            _bitmap.value = bitmap
            _previewBitmap.value = imageScaler.scaleUntilCanShow(bitmap)
            delay(500)
            _isImageLoading.value = false
        }
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    fun saveBitmaps(oneTimeSaveLocationUri: String?) {
        val uris = uris.orEmpty()
        if (uris.isEmpty() || !canSave || savingJob?.isActive == true) return

        val replacement = replacementMetadata()
        val tagsToRemove = selectedTags.ifEmpty { MetadataTag.entries }
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            try {
                val results = uris.mapIndexed { index, uri ->
                    val result = runSuspendCatching {
                        fileController.save(
                            saveTarget = saveTarget(
                                uri,
                                index,
                                tagsToRemove,
                                replacement?.invoke(index)
                            ),
                            keepOriginalMetadata = false,
                            oneTimeSaveLocationUri = oneTimeSaveLocationUri,
                            replaceExif = replacement != null
                        )
                    }.getOrElse { SaveResult.Error.Exception(it) }
                    _done.value = index + 1
                    updateProgress(done = done, total = uris.size)
                    result
                }
                parseSaveResults(results.onSuccess(::registerSave))
            } finally {
                _isSaving.value = false
            }
        }
    }

    private fun replacementMetadata(): ((Int) -> Metadata)? {
        if (!replaceExif) return null

        val preset = replacementPreset
        if (preset == ExifReplacementPreset.Custom) {
            val tags = customTags
            return { Metadata(tags) }
        }

        val settings = replacementSettings
        return { Metadata(preset.generateTags(settings, it)) }
    }

    private suspend fun saveTarget(
        uri: Uri,
        index: Int,
        tagsToRemove: List<MetadataTag>,
        replacement: Metadata?
    ): ImageSaveTarget {
        val originalUri = uri.toString()
        val (imageInfo, metadata) = if (replacement == null) {
            val imageData = imageGetter.getImage(originalUri)
                ?: error(getString(R.string.failed_to_open))
            imageData.imageInfo to imageData.metadata?.clearAttributes(tagsToRemove)
        } else withContext(ioDispatcher) {
            val size = uri.imageSize()
            ImageInfo(
                width = size?.width ?: 0,
                height = size?.height ?: 0,
                imageFormat = ImageFormat[imageGetter.getExtension(originalUri)],
                originalUri = originalUri
            ) to replacement
        }
        return ImageSaveTarget(
            imageInfo = imageInfo,
            originalUri = originalUri,
            sequenceNumber = index,
            metadata = metadata,
            data = ByteArray(0),
            readFromUriInsteadOfData = true
        )
    }

    fun updateSelectedUri(
        uri: Uri
    ) = componentScope.launch {
        _isImageLoading.value = true
        _selectedUri.value = uri
        imageGetter.getImageAsync(
            uri = uri.toString(),
            originalSize = false,
            onGetImage = {
                updateBitmap(it.image)
            },
            onFailure = {
                _isImageLoading.value = false
                AppToastHost.showFailureToast(it)
            }
        )
    }

    fun shareBitmaps() {
        cacheImages { uris ->
            if (uris.isNotEmpty()) {
                shareProvider.shareUris(uris.map(Uri::toString))
                AppToastHost.showConfetti()
            }
        }
    }

    fun cancelSaving() {
        _isSaving.update { false }
        savingJob?.cancel()
        savingJob = null
    }

    fun cacheCurrentImage(onComplete: (Uri) -> Unit) {
        cacheImages(
            uris = listOfNotNull(selectedUri),
            onComplete = {
                if (it.isNotEmpty()) onComplete(it.first())
            }
        )
    }

    fun addTag(tag: MetadataTag) {
        _removalPreset.value = ExifRemovalPreset.Custom
        if (tag in selectedTags) {
            _selectedTags.update { it - tag }
        } else {
            _selectedTags.update { it + tag }
        }
    }

    fun setRemovalPreset(preset: ExifRemovalPreset) {
        if (preset == ExifRemovalPreset.Custom) return

        _removalPreset.value = preset
        _selectedTags.value = preset.tags
    }

    fun cacheImages(
        uris: List<Uri>? = this.uris,
        onComplete: suspend (List<Uri>) -> Unit
    ) {
        val images = uris.orEmpty()
        if (images.isEmpty() || !canSave || savingJob?.isActive == true) return

        val replacement = replacementMetadata()
        val tagsToRemove = selectedTags.ifEmpty { MetadataTag.entries }
        val sourceUris = this.uris.orEmpty()
        savingJob = trackProgress {
            _isSaving.value = true
            _done.value = 0
            try {
                var failure: Throwable? = null
                val cached = images.mapIndexedNotNull { index, uri ->
                    val result = runSuspendCatching {
                        val imageIndex = if (images === sourceUris) index else {
                            sourceUris.indexOf(uri).takeIf { it >= 0 } ?: index
                        }
                        val target =
                            saveTarget(uri, index, tagsToRemove, replacement?.invoke(imageIndex))
                        cacheImage(target, replaceExif = replacement != null)
                    }.onFailure {
                        if (failure == null) failure = it
                    }
                    _done.value = index + 1
                    updateProgress(done = done, total = images.size)
                    result.getOrNull()
                }
                failure?.let(AppToastHost::showFailureToast)
                onComplete(cached)
            } finally {
                _isSaving.value = false
            }
        }
    }

    private suspend fun cacheImage(target: ImageSaveTarget, replaceExif: Boolean): Uri {
        val filename = filenameCreator.constructImageFilename(
            saveTarget = target,
            forceNotAddSizeInFilename = target.imageInfo.width <= 0 || target.imageInfo.height <= 0
        )
        val cachedUri = shareProvider.cacheDataOrThrow(filename) { output ->
            val result = fileController.transferBytes(target.originalUri, output)
            if (result is SaveResult.Error) throw result.throwable
        }
        withContext(ioDispatcher) {
            fileController.writeMetadata(cachedUri, target.metadata, replaceExif = replaceExif)
        }
        return cachedUri.toUri()
    }

    fun selectLeftUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 && uris.orEmpty().size > 1 }
            ?.let {
                uris?.leftFrom(it)
            }
            ?.let(::updateSelectedUri)
    }

    fun selectRightUri() {
        uris
            ?.indexOf(selectedUri ?: Uri.EMPTY)
            ?.takeIf { it >= 0 && uris.orEmpty().size > 1 }
            ?.let {
                uris?.rightFrom(it)
            }
            ?.let(::updateSelectedUri)
    }


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): DeleteExifComponent
    }
}

private data class ExifTemplates(
    val tagsByName: Map<String, Map<String, String>> = emptyMap()
)