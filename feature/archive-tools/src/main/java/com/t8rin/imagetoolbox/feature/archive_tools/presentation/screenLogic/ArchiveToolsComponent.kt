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

package com.t8rin.imagetoolbox.feature.archive_tools.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.archive.ArchiveCompressionLevel
import com.t8rin.archive.ArchiveEncryptionStatus
import com.t8rin.archive.ArchiveEntryInfo
import com.t8rin.archive.ArchiveFormat
import com.t8rin.archive.SevenZipCompressionMethod
import com.t8rin.archive.ZipCompressionMethod
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
import com.t8rin.imagetoolbox.feature.archive_tools.domain.ArchiveManager
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.ArchiveExtractionOptions
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.ArchiveMode
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.hasSupportedArchiveExtension
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job

internal enum class ArchivePassphraseStatus {
    Required,
    Checking,
    Verified,
    Invalid
}

class ArchiveToolsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    private val archiveManager: ArchiveManager,
    private val shareProvider: ShareProvider,
    private val fileController: FileController,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    init {
        debounce {
            initialUris?.let { uris ->
                if (
                    uris.isNotEmpty() &&
                    uris.all { it.filename()?.hasSupportedArchiveExtension() == true }
                ) {
                    setMode(ArchiveMode.Extract)
                }
                setUris(uris)
            }
        }
    }

    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris by _uris

    private val _mode = mutableStateOf(ArchiveMode.Archive)
    val mode by _mode

    private val _format = mutableStateOf(ArchiveFormat.Zip)
    val format by _format

    private val _zipCompressionMethod = mutableStateOf(ZipCompressionMethod.Deflate)
    val zipCompressionMethod by _zipCompressionMethod

    private val _sevenZipCompressionMethod = mutableStateOf(SevenZipCompressionMethod.Lzma2)
    val sevenZipCompressionMethod by _sevenZipCompressionMethod

    private val _compressionLevel = mutableStateOf(ArchiveCompressionLevel.Normal)
    val compressionLevel by _compressionLevel

    val canSetCompressionLevel: Boolean
        get() = format.supportsCompressionLevel(
            zipCompressionMethod = zipCompressionMethod,
            sevenZipCompressionMethod = sevenZipCompressionMethod
        )

    private val _passphrase = mutableStateOf("")
    val passphrase by _passphrase

    private val _protectWithPassword = mutableStateOf(false)
    val protectWithPassword by _protectWithPassword

    val canProtectWithPassword: Boolean
        get() = when (format) {
            ArchiveFormat.Zip -> zipCompressionMethod.supportsEncryption
            ArchiveFormat.SevenZip -> sevenZipCompressionMethod.supportsEncryption
            else -> false
        }

    private val _archiveEncryptionStatuses =
        mutableStateOf<Map<Uri, ArchiveEncryptionStatus?>>(emptyMap())

    private val _archivePassphrases = mutableStateOf<Map<Uri, String>>(emptyMap())

    private val _archivePassphraseStatuses =
        mutableStateOf<Map<Uri, ArchivePassphraseStatus>>(emptyMap())

    private val _archivePasswordRequestUri = mutableStateOf<Uri?>(null)
    val archivePasswordRequestUri by _archivePasswordRequestUri

    val canExtract: Boolean
        get() = extractionOptions.selectedEntries?.isNotEmpty() != false && uris.all { uri ->
            when (archiveEncryptionStatus(uri)) {
                ArchiveEncryptionStatus.Unsupported -> false
                ArchiveEncryptionStatus.PasswordRequired -> {
                    if (uris.size == 1) {
                        archivePassphrase(uri).isNotEmpty()
                    } else {
                        archivePassphraseStatus(uri) == ArchivePassphraseStatus.Verified
                    }
                }

                ArchiveEncryptionStatus.None -> true
                null -> uris.size == 1
            }
        }

    private val _extractionOptions = mutableStateOf(ArchiveExtractionOptions())
    val extractionOptions by _extractionOptions

    private val _archiveEntries = mutableStateOf<List<ArchiveEntryInfo>?>(null)
    val archiveEntries by _archiveEntries

    private val _isLoadingArchiveEntries = mutableStateOf(false)
    val isLoadingArchiveEntries by _isLoadingArchiveEntries

    private val _showArchiveEntries = mutableStateOf(false)
    val showArchiveEntries by _showArchiveEntries

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    fun setUris(newUris: List<Uri>) {
        val selectedUris = newUris
            .distinct()
            .filter {
                mode != ArchiveMode.Extract ||
                        it.filename()?.hasSupportedArchiveExtension() == true
            }
        if (mode == ArchiveMode.Extract && newUris.isNotEmpty() && selectedUris.isEmpty()) {
            AppToastHost.showFailureToast(R.string.select_archive_to_start)
            return
        }
        if (selectedUris.size > 1 && !format.supportsMultipleFiles) {
            setFormat(ArchiveFormat.Zip)
        }
        if (selectedUris != uris) {
            clearArchiveEntries()
        }
        _uris.update {
            selectedUris
        }
        if (selectedUris.isEmpty()) registerChangesCleared() else registerChanges()
        if (mode == ArchiveMode.Extract) {
            _archivePassphrases.update { current ->
                selectedUris.associateWith { current[it].orEmpty() }
            }
            _archivePassphraseStatuses.update { current ->
                selectedUris.mapNotNull { uri ->
                    current[uri]?.let { uri to it }
                }.toMap()
            }
            _archiveEncryptionStatuses.value = selectedUris.associateWith { null }
            if (archivePasswordRequestUri !in selectedUris) {
                _archivePasswordRequestUri.value = null
            }
            detectArchiveEncryption(selectedUris)
        }
    }

    fun setMode(mode: ArchiveMode) {
        if (mode == this.mode) return
        _mode.update { mode }
        _uris.update { emptyList() }
        encryptionDetectionJob = null
        _archiveEncryptionStatuses.value = emptyMap()
        _archivePassphrases.value = emptyMap()
        _archivePassphraseStatuses.value = emptyMap()
        _archivePasswordRequestUri.value = null
        clearArchiveEntries()
        _passphrase.update { "" }
        _protectWithPassword.update { false }
        _extractionOptions.update { ArchiveExtractionOptions() }
        registerChangesCleared()
    }

    fun setFormat(format: ArchiveFormat) {
        if (format == this.format) return
        if (!format.supportsMultipleFiles && uris.size > 1) return
        _format.update { format }
        if (!canProtectWithPassword) {
            _protectWithPassword.update { false }
            _passphrase.update { "" }
        }
        registerChanges()
    }

    fun setZipCompressionMethod(method: ZipCompressionMethod) {
        if (method == zipCompressionMethod) return
        _zipCompressionMethod.update { method }
        if (!canProtectWithPassword) {
            _protectWithPassword.update { false }
            _passphrase.update { "" }
        }
        registerChanges()
    }

    fun setSevenZipCompressionMethod(method: SevenZipCompressionMethod) {
        if (method == sevenZipCompressionMethod) return
        _sevenZipCompressionMethod.update { method }
        if (!canProtectWithPassword) {
            _protectWithPassword.update { false }
            _passphrase.update { "" }
        }
        registerChanges()
    }

    fun setCompressionLevel(level: ArchiveCompressionLevel) {
        if (level == compressionLevel) return
        _compressionLevel.update { level }
        registerChanges()
    }

    fun setProtectWithPassword(protect: Boolean) {
        if (protect == protectWithPassword) return
        _protectWithPassword.update { protect && canProtectWithPassword }
        if (!protect) _passphrase.update { "" }
        registerChanges()
    }

    fun setPassphrase(passphrase: String) {
        if (passphrase == this.passphrase) return
        _passphrase.update { passphrase }
        registerChanges()
    }

    fun archiveEncryptionStatus(uri: Uri): ArchiveEncryptionStatus? =
        _archiveEncryptionStatuses.value[uri]

    fun archivePassphrase(uri: Uri): String = _archivePassphrases.value[uri].orEmpty()

    internal fun archivePassphraseStatus(uri: Uri): ArchivePassphraseStatus? =
        _archivePassphraseStatuses.value[uri]

    fun requestArchivePassphrase(uri: Uri) {
        if (archiveEncryptionStatus(uri) == ArchiveEncryptionStatus.PasswordRequired) {
            _archivePasswordRequestUri.value = uri
        }
    }

    fun dismissArchivePassphraseRequest() {
        _archivePasswordRequestUri.value = null
    }

    fun setArchivePassphrase(uri: Uri, passphrase: String) {
        if (uri !in uris || archivePassphrase(uri) == passphrase) return
        if (uris.singleOrNull() == uri) {
            clearArchiveEntries()
        }
        _archivePassphrases.update { it + (uri to passphrase) }
        if (archiveEncryptionStatus(uri) == ArchiveEncryptionStatus.PasswordRequired) {
            _archivePassphraseStatuses.update {
                it + (uri to ArchivePassphraseStatus.Required)
            }
        }
        registerChanges()
    }

    fun verifyArchivePassphrase(uri: Uri, passphrase: String) {
        if (
            uri !in uris ||
            passphrase.isEmpty() ||
            archiveEncryptionStatus(uri) != ArchiveEncryptionStatus.PasswordRequired
        ) return

        _archivePassphrases.update { it + (uri to passphrase) }
        _archivePassphraseStatuses.update {
            it + (uri to ArchivePassphraseStatus.Checking)
        }
        registerChanges()

        componentScope.launch(defaultDispatcher) {
            val verified = runCatching {
                archiveManager.verifyArchivePassphrase(
                    archive = uri.toString(),
                    passphrase = passphrase
                )
            }.getOrDefault(false)
            if (uri in uris && archivePassphrase(uri) == passphrase) {
                _archivePassphraseStatuses.update {
                    it + (uri to if (verified) {
                        ArchivePassphraseStatus.Verified
                    } else {
                        ArchivePassphraseStatus.Invalid
                    })
                }
                if (verified && archivePasswordRequestUri == uri) {
                    _archivePasswordRequestUri.value = null
                }
            }
        }
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

    fun openArchiveEntries() {
        val archive = uris.singleOrNull() ?: return
        if (
            archiveEncryptionStatus(archive) == ArchiveEncryptionStatus.PasswordRequired &&
            archivePassphrase(archive).isEmpty()
        ) {
            AppToastHost.showFailureToast(R.string.archive_password_required_sub)
            return
        }
        _showArchiveEntries.value = true
        if (archiveEntries == null) {
            loadArchiveEntries(archive)
        }
    }

    fun dismissArchiveEntries() {
        _showArchiveEntries.value = false
    }

    fun retryLoadingArchiveEntries() {
        uris.singleOrNull()?.let(::loadArchiveEntries)
    }

    fun setArchiveEntrySelected(path: String, selected: Boolean) {
        if (archiveEntries?.any { it.path == path } != true) return
        _extractionOptions.update { options ->
            val current = options.selectedEntries ?: return@update options
            options.copy(
                selectedEntries = if (selected) current + path else current - path
            )
        }
        registerChanges()
    }

    fun setAllArchiveEntriesSelected(selected: Boolean) {
        val entries = archiveEntries ?: return
        _extractionOptions.update {
            it.copy(
                selectedEntries = if (selected) {
                    entries.mapTo(mutableSetOf(), ArchiveEntryInfo::path)
                } else {
                    emptySet()
                }
            )
        }
        registerChanges()
    }

    private var savingJob: Job? by smartJob {
        _isSaving.update { false }
    }

    private var encryptionDetectionJob: Job? by smartJob()

    private var archiveEntriesJob: Job? by smartJob {
        _isLoadingArchiveEntries.value = false
    }

    private fun loadArchiveEntries(archive: Uri) {
        val passphrase = archivePassphrase(archive).takeIf(String::isNotEmpty)
        archiveEntriesJob = componentScope.launch(defaultDispatcher) {
            _isLoadingArchiveEntries.value = true
            runCatching {
                archiveManager.listEntries(
                    archive = archive.toString(),
                    passphrase = passphrase
                ).filterNot(ArchiveEntryInfo::isDirectory)
            }.onSuccess { entries ->
                if (
                    uris.singleOrNull() == archive &&
                    archivePassphrase(archive).takeIf(String::isNotEmpty) == passphrase
                ) {
                    _archiveEntries.value = entries
                    _extractionOptions.update {
                        it.copy(
                            selectedEntries = entries.mapTo(
                                mutableSetOf(),
                                ArchiveEntryInfo::path
                            )
                        )
                    }
                }
            }.onFailure(AppToastHost::showFailureToast)
            _isLoadingArchiveEntries.value = false
        }
    }

    private fun clearArchiveEntries() {
        archiveEntriesJob = null
        _archiveEntries.value = null
        _isLoadingArchiveEntries.value = false
        _showArchiveEntries.value = false
        _extractionOptions.update { it.copy(selectedEntries = null) }
    }

    private fun detectArchiveEncryption(uris: List<Uri>) {
        encryptionDetectionJob = componentScope.launch(defaultDispatcher) {
            uris.forEach { uri ->
                val status = runCatching {
                    archiveManager.getArchiveEncryptionStatus(uri.toString())
                }.getOrNull()
                if (mode == ArchiveMode.Extract && uri in this@ArchiveToolsComponent.uris) {
                    _archiveEncryptionStatuses.update { it + (uri to status) }
                    _archivePassphraseStatuses.update { current ->
                        if (status == ArchiveEncryptionStatus.PasswordRequired) {
                            current + (uri to current.getOrDefault(
                                uri,
                                ArchivePassphraseStatus.Required
                            ))
                        } else {
                            current - uri
                        }
                    }
                }
            }
        }
    }

    fun createTargetFilename(): String {
        if (format.isRaw) {
            uris.singleOrNull()?.filename()?.let { return "$it.${format.extension}" }
        }
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
                    archiveManager.archive(
                        files = uris.map { it.toString() },
                        destination = output,
                        format = format,
                        zipCompressionMethod = zipCompressionMethod,
                        sevenZipCompressionMethod = sevenZipCompressionMethod,
                        compressionLevel = compressionLevel,
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
                    archiveManager.archive(
                        files = uris.map(Uri::toString),
                        destination = output,
                        format = format,
                        zipCompressionMethod = zipCompressionMethod,
                        sevenZipCompressionMethod = sevenZipCompressionMethod,
                        compressionLevel = compressionLevel,
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
            val archives = uris
            if (archives.isEmpty()) {
                _isSaving.value = false
                return@trackProgress
            }
            runSuspendCatching {
                _done.update { 0 }
                _left.update { archives.size }
                archives.forEachIndexed { index, archive ->
                    archiveManager.extract(
                        archive = archive.toString(),
                        destinationFolder = destinationFolder.toString(),
                        passphrase = archivePassphrase(archive).takeIf(String::isNotEmpty),
                        options = extractionOptions,
                        onProgress = {}
                    )
                    _done.update { index + 1 }
                    updateProgress(done = done, total = left)
                }
                registerSave()
                parseFileSaveResult(SaveResult.Success(savingPath = ""))
            }.onFailure(AppToastHost::showFailureToast)
            _isSaving.value = false
        }
    }

    fun shareExtractedFiles() {
        savingJob = trackProgress {
            _isSaving.value = true
            val archives = uris
            if (archives.isEmpty()) {
                _isSaving.value = false
                return@trackProgress
            }
            runSuspendCatching {
                _done.update { 0 }
                _left.update { archives.size }
                val extractedFiles = buildList {
                    archives.forEachIndexed { index, archive ->
                        addAll(
                            archiveManager.extractToCache(
                                archive = archive.toString(),
                                passphrase = archivePassphrase(archive).takeIf(String::isNotEmpty),
                                options = extractionOptions,
                                onProgress = {}
                            )
                        )
                        _done.update { index + 1 }
                        updateProgress(done = done, total = left)
                    }
                }
                check(extractedFiles.isNotEmpty()) {
                    "Archive contains no shareable files"
                }
                if (extractedFiles.size == 1) {
                    shareProvider.shareUri(
                        uri = extractedFiles.single(),
                        onComplete = AppToastHost::showConfetti
                    )
                } else {
                    shareProvider.shareUris(extractedFiles)
                    AppToastHost.showConfetti()
                }
            }.onFailure(AppToastHost::showFailureToast)
            _isSaving.value = false
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

    fun removeUri(uri: Uri) {
        if (uri !in uris) return

        val remainingUris = uris - uri
        clearArchiveEntries()
        _archiveEncryptionStatuses.update { it - uri }
        _archivePassphrases.update { it - uri }
        _archivePassphraseStatuses.update { it - uri }
        if (archivePasswordRequestUri == uri) {
            _archivePasswordRequestUri.value = null
        }
        _uris.value = remainingUris

        if (remainingUris.isEmpty()) registerChangesCleared() else registerChanges()
    }

    fun addUris(list: List<Uri>) {
        setUris(uris + list)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
        ): ArchiveToolsComponent
    }
}
