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

package com.t8rin.imagetoolbox.feature.zip.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.archive.ArchiveEncryptionStatus
import com.t8rin.archive.ArchiveFormat
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.zip.domain.ZipManager
import com.t8rin.imagetoolbox.feature.zip.domain.model.ArchiveExtractionOptions
import com.t8rin.imagetoolbox.feature.zip.domain.model.ArchiveMode
import com.t8rin.imagetoolbox.feature.zip.domain.model.hasSupportedArchiveExtension
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

class ZipComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    private val zipManager: ZipManager,
    private val shareProvider: ShareProvider,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let(::setUris)
        }
    }

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris by _uris

    private val _mode = mutableStateOf(ArchiveMode.Archive)
    val mode by _mode

    private val _format = mutableStateOf(ArchiveFormat.Zip)
    val format by _format

    private val _passphrase = mutableStateOf("")
    val passphrase by _passphrase

    private val _protectWithPassword = mutableStateOf(false)
    val protectWithPassword by _protectWithPassword

    private val _archiveEncryptionStatus = mutableStateOf<ArchiveEncryptionStatus?>(null)
    val archiveEncryptionStatus by _archiveEncryptionStatus

    private val _extractedEntries = mutableIntStateOf(0)
    val extractedEntries by _extractedEntries

    private val _extractionOptions = mutableStateOf(ArchiveExtractionOptions())
    val extractionOptions by _extractionOptions

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    fun setUris(newUris: List<Uri>) {
        val selectedArchive = newUris.firstOrNull()
        if (
            mode == ArchiveMode.Extract &&
            selectedArchive != null &&
            selectedArchive.filename()?.hasSupportedArchiveExtension() != true
        ) {
            AppToastHost.showFailureToast(R.string.select_archive_to_start)
            return
        }
        val selectedUris = if (mode == ArchiveMode.Extract) {
            newUris.take(1)
        } else {
            newUris.distinct()
        }
        _uris.update {
            selectedUris
        }
        if (selectedUris.isEmpty()) registerChangesCleared() else registerChanges()
        resetCalculatedData()
        if (mode == ArchiveMode.Extract) {
            _passphrase.update { "" }
            _archiveEncryptionStatus.value = null
            selectedUris.firstOrNull()?.let(::detectArchiveEncryption)
        }
    }

    fun setMode(mode: ArchiveMode) {
        if (mode == this.mode) return
        _mode.update { mode }
        _uris.update { emptyList() }
        encryptionDetectionJob = null
        _archiveEncryptionStatus.value = null
        _passphrase.update { "" }
        _protectWithPassword.update { false }
        _extractionOptions.update { ArchiveExtractionOptions() }
        resetCalculatedData()
        registerChangesCleared()
    }

    fun setFormat(format: ArchiveFormat) {
        if (format == this.format) return
        _format.update { format }
        if (!format.supportsEncryption) {
            _protectWithPassword.update { false }
            _passphrase.update { "" }
        }
        resetCalculatedData()
        registerChanges()
    }

    fun setProtectWithPassword(protect: Boolean) {
        if (protect == protectWithPassword) return
        _protectWithPassword.update { protect && format.supportsEncryption }
        if (!protect) _passphrase.update { "" }
        resetCalculatedData()
        registerChanges()
    }

    fun setPassphrase(passphrase: String) {
        if (passphrase == this.passphrase) return
        _passphrase.update { passphrase }
        _extractedEntries.update { 0 }
        registerChanges()
    }

    fun setCreateSubfolder(createSubfolder: Boolean) {
        _extractionOptions.update { it.copy(createSubfolder = createSubfolder) }
        registerChanges()
    }

    fun setPreserveDirectoryStructure(preserve: Boolean) {
        _extractionOptions.update { it.copy(preserveDirectoryStructure = preserve) }
        registerChanges()
    }

    fun setSkipHiddenFiles(skip: Boolean) {
        _extractionOptions.update { it.copy(skipHiddenFiles = skip) }
        registerChanges()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    private var encryptionDetectionJob: Job? by smartJob()

    private fun detectArchiveEncryption(uri: Uri) {
        encryptionDetectionJob = componentScope.launch(defaultDispatcher) {
            val status = runCatching {
                zipManager.getArchiveEncryptionStatus(uri.toString())
            }.getOrNull()
            if (mode == ArchiveMode.Extract && uris.firstOrNull() == uri) {
                _archiveEncryptionStatus.value = status
            }
        }
    }

    fun createTargetFilename(): String {
        val count = uris.size.takeIf { it > 1 }?.let { "($it)" }.orEmpty()
        return "${format.title}${count}_${timestamp()}.${format.extension}"
    }

    fun startArchiving(destination: Uri) {
        savingJob = trackProgress {
            _isSaving.value = true
            if (uris.isEmpty()) {
                _isSaving.value = false
                return@trackProgress
            }
            runSuspendCatching {
                _done.update { 0 }
                _left.update { uris.size }
                fileController.writeBytes(destination.toString()) { output ->
                    zipManager.archive(
                        files = uris.map { it.toString() },
                        destination = output,
                        format = format,
                        passphrase = passphrase.takeIf {
                            protectWithPassword && it.isNotEmpty()
                        },
                        onProgress = {
                            _done.update { it + 1 }
                            updateProgress(
                                done = done,
                                total = left
                            )
                        }
                    )
                }.also(::parseFileSaveResult).onSuccess(::registerSave)
            }.onFailure(AppToastHost::showFailureToast)
            _isSaving.value = false
        }
    }

    fun shareArchive() {
        savingJob = trackProgress {
            _isSaving.value = true
            if (uris.isEmpty()) {
                _isSaving.value = false
                return@trackProgress
            }
            runSuspendCatching {
                _done.update { 0 }
                _left.update { uris.size }
                val cachedArchive = shareProvider.cacheDataOrThrow(
                    filename = createTargetFilename()
                ) { output ->
                    zipManager.archive(
                        files = uris.map(Uri::toString),
                        destination = output,
                        format = format,
                        passphrase = passphrase.takeIf {
                            protectWithPassword && it.isNotEmpty()
                        },
                        onProgress = {
                            _done.update { it + 1 }
                            updateProgress(
                                done = done,
                                total = left
                            )
                        }
                    )
                }
                shareProvider.shareUri(
                    uri = cachedArchive,
                    type = MimeType.Single(format.mimeType),
                    onComplete = AppToastHost::showConfetti
                )
            }.onFailure(AppToastHost::showFailureToast)
            _isSaving.value = false
        }
    }

    fun startExtraction(destinationFolder: Uri) {
        savingJob = trackProgress {
            _isSaving.value = true
            val archive = uris.firstOrNull()
            if (archive == null) {
                _isSaving.value = false
                return@trackProgress
            }
            runSuspendCatching {
                _done.update { 0 }
                _left.update { -1 }
                _extractedEntries.intValue = zipManager.extract(
                    archive = archive.toString(),
                    destinationFolder = destinationFolder.toString(),
                    passphrase = passphrase.takeIf(String::isNotEmpty),
                    options = extractionOptions,
                    onProgress = {
                        _done.update { it + 1 }
                    }
                )
                registerSave()
                parseFileSaveResult(SaveResult.Success(savingPath = ""))
            }.onFailure(AppToastHost::showFailureToast)
            _isSaving.value = false
        }
    }

    private fun resetCalculatedData() {
        _extractedEntries.update { 0 }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun removeUri(uri: Uri) {
        setUris(uris - uri)
    }

    fun addUris(list: List<Uri>) {
        if (mode == ArchiveMode.Archive) setUris(uris + list)
        else setUris(list.take(1))
    }


    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
        ): ZipComponent
    }
}