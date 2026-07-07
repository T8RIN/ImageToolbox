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

package com.t8rin.imagetoolbox.feature.batchrename.presentation.screenLogic

import android.content.IntentSender
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.batchrename.domain.RenameManager
import com.t8rin.imagetoolbox.feature.batchrename.domain.helper.FilenamePatternResolver
import com.t8rin.imagetoolbox.feature.batchrename.domain.helper.RenamePatterns
import com.t8rin.imagetoolbox.feature.batchrename.domain.helper.RenameValidator
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.DateSource
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameFile
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameResult
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameTarget
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameValidationError
import com.t8rin.imagetoolbox.feature.batchrename.presentation.components.RenamePreview
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BatchRenameComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    private val manager: RenameManager,
    settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val settings = settingsProvider.settingsState.value

    private val _files: MutableState<List<RenameFile>> = mutableStateOf(emptyList())
    val files by _files

    private val _pattern: MutableState<String> = mutableStateOf(RenamePatterns.Default)
    val pattern by _pattern

    private val _dateSource: MutableState<DateSource> = mutableStateOf(DateSource.Current)
    val dateSource by _dateSource

    private val _manualDate: MutableState<Long> = mutableLongStateOf(System.currentTimeMillis())
    val manualDate by _manualDate

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading by _isLoading

    private val _pendingPermission: MutableState<IntentSender?> = mutableStateOf(null)
    val pendingPermission by _pendingPermission

    private val timestamp: Long = System.currentTimeMillis()

    private var pendingPermissionUris: Set<Uri> = emptySet()
    private val writableUris = mutableSetOf<Uri>()

    private var renameJob: Job? by smartJob {
        _isLoading.value = false
    }

    val previews: StateFlow<List<RenamePreview>> = combine(
        settingsProvider.settingsState,
        snapshotFlow { files },
        snapshotFlow { pattern },
        snapshotFlow { dateSource },
        snapshotFlow { manualDate }
    ) { _, _, _, dateSource, manualDate ->
        createTargets().map { target ->
            RenamePreview(
                uri = target.file.uri.toUri(),
                originalName = target.file.originalName,
                newName = target.newName,
                usedFallbackDate = target.file.dateFor(
                    source = dateSource,
                    currentDate = timestamp,
                    manualDate = manualDate
                ) == null,
                isChanged = target.file.originalName != target.newName
            )
        }
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val validationError: StateFlow<RenameValidationError?> = combine(
        snapshotFlow { pattern },
        previews
    ) { pattern, previews ->
        RenameValidator.validate(pattern, previews)
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        initialUris?.let(::setUris)
    }

    fun setUris(uris: List<Uri>) {
        componentScope.launch {
            _isLoading.value = true
            _files.value = manager.readFiles(uris.map { it.toString() })
            writableUris.clear()
            _isLoading.value = false
        }
    }

    fun addUris(uris: List<Uri>) {
        setUris((files.map { it.uri.toUri() } + uris).distinct())
    }

    fun removeFile(uri: Uri) {
        _files.update { current -> current.filterNot { it.uri.toUri() == uri } }
    }

    fun updatePattern(value: String) {
        _pattern.value = value
    }

    fun updateDateSource(value: DateSource) {
        _dateSource.value = value
    }

    fun updateManualDate(value: Long) {
        _manualDate.value = value
    }

    fun rename() {
        if (files.isEmpty() || validationError.value != null) return

        renameJob = componentScope.launch {
            _isLoading.value = true
            val targetUris = createTargets()
            val result = manager.rename(
                files = targetUris,
                writableUris = writableUris.map { it.toString() }.toSet()
            )

            when (result) {
                is RenameResult.Success -> {
                    _pattern.value = RenamePatterns.Default
                    _files.value = emptyList()
                    writableUris.clear()
                    _isLoading.value = false
                    AppToastHost.showToast(
                        message = R.string.batch_rename_success,
                        icon = Icons.Rounded.Save
                    )
                    AppToastHost.showConfetti()
                }

                is RenameResult.PermissionRequired -> {
                    pendingPermissionUris = result.uris.mapTo(mutableSetOf()) { it.toUri() }
                    _pendingPermission.value = result.intentSender as? IntentSender
                }

                is RenameResult.Failure -> {
                    _isLoading.value = false
                    result.cause?.let(AppToastHost::showFailureToast)
                        ?: AppToastHost.showFailureToast(R.string.batch_rename_failed)
                }
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _pendingPermission.value = null
        if (granted) {
            writableUris += pendingPermissionUris
            pendingPermissionUris = emptySet()
            _isLoading.value = false
            rename()
        } else {
            pendingPermissionUris = emptySet()
            _isLoading.value = false
            AppToastHost.showFailureToast(R.string.permission_not_granted)
        }
    }

    fun cancel() {
        renameJob = null
    }

    private fun createTargets(): List<RenameTarget> {
        val resolver = FilenamePatternResolver(
            prefix = settings.filenamePrefix,
            suffix = settings.filenameSuffix,
            randomSeed = timestamp
        )
        val pattern = RenamePatterns.sanitize(pattern)

        return files.mapIndexed { index, file ->
            val date = file.dateFor(
                source = dateSource,
                currentDate = timestamp,
                manualDate = manualDate
            ) ?: timestamp
            RenameTarget(
                file = file,
                newName = resolver.resolve(
                    pattern = pattern,
                    file = file,
                    sequence = index + 1,
                    dateMillis = date
                )
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit
        ): BatchRenameComponent
    }
}
