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

package com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.screenLogic

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.data.saving.FileControllerEventEmitter
import com.t8rin.imagetoolbox.core.data.saving.FileDeletionResult
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.DuplicateFinder
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper.DuplicateGrouping
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisProgress
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisResult
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateGroup
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

class DuplicateFinderComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val initialUris: List<Uri>?,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val duplicateFinder: DuplicateFinder,
    private val fileControllerEventEmitter: FileControllerEventEmitter,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _sourceUris: MutableState<List<Uri>> = mutableStateOf(emptyList())
    val sourceUris by _sourceUris

    private val _analysisResult: MutableState<DuplicateAnalysisResult?> = mutableStateOf(null)
    val analysisResult by _analysisResult

    private val _groups: MutableState<List<DuplicateGroup>> = mutableStateOf(emptyList())
    val groups by _groups

    private val _sensitivity = mutableIntStateOf(DuplicateGrouping.DEFAULT_SENSITIVITY)
    val sensitivity by _sensitivity

    private val _selectedUris: MutableState<Set<String>> = mutableStateOf(emptySet())
    val selectedUris by _selectedUris

    private val _progress: MutableState<DuplicateAnalysisProgress?> = mutableStateOf(null)
    val progress by _progress

    private val _isAnalyzing: MutableState<Boolean> = mutableStateOf(false)
    val isAnalyzing by _isAnalyzing

    private val _isDeleting: MutableState<Boolean> = mutableStateOf(false)
    val isDeleting by _isDeleting

    private var analysisJob: Job? by smartJob {
        _isAnalyzing.value = false
    }

    val selectedSizeBytes: Long
        get() = analysisResult?.items
            ?.asSequence()
            ?.filter { it.uri in selectedUris }
            ?.sumOf { it.sizeBytes }
            ?: 0L

    val previewUris: List<Uri>
        get() = groups
            .flatMap(DuplicateGroup::sortedItems)
            .distinctBy { it.uri }
            .map { it.uri.toUri() }

    init {
        initialUris
            ?.takeIf { it.isNotEmpty() }
            ?.let(::setUris)
    }

    fun setUris(uris: List<Uri>) {
        val distinctUris = uris.distinct()
        _sourceUris.value = distinctUris
        _selectedUris.value = emptySet()
        if (distinctUris.isEmpty()) {
            cancelAnalysis()
            _analysisResult.value = null
            _groups.value = emptyList()
        } else {
            analyze()
        }
    }

    fun addUris(uris: List<Uri>) {
        setUris((sourceUris + uris).distinct())
    }

    private fun analyze() {
        if (sourceUris.isEmpty()) return

        val requestedUris = sourceUris.map(Uri::toString)
        analysisJob = trackProgress {
            _isAnalyzing.value = true
            _progress.value = null
            try {
                val result = duplicateFinder.findDuplicates(
                    uris = requestedUris,
                    sensitivity = sensitivity,
                    onProgress = {
                        updateProgress(
                            done = it.processed,
                            total = it.total
                        )
                        _progress.value = it
                    }
                )
                _analysisResult.value = result
                updateGroups(result.groups)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                AppToastHost.showFailureToast(throwable)
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun cancelAnalysis() {
        analysisJob = null
        _progress.value = null
    }

    fun updateSensitivity(value: Int) {
        val normalized = DuplicateGrouping.normalizeSensitivity(value)
        if (_sensitivity.intValue == normalized) return

        _sensitivity.intValue = normalized
        analysisResult?.let { result ->
            val regrouped = DuplicateGrouping.regroup(
                items = result.items,
                sensitivity = normalized
            )
            _analysisResult.value = result.copy(groups = regrouped)
            updateGroups(regrouped)
        }
    }

    fun toggleSelection(uri: String) {
        _selectedUris.update { selected ->
            if (uri in selected) selected - uri else selected + uri
        }
    }

    fun selectExactDuplicates() {
        _selectedUris.value = DuplicateGrouping.selectAllDuplicates(
            groups.filter { it.type == DuplicateType.Exact }
        )
    }

    fun selectAllExceptRecommended() {
        _selectedUris.value = DuplicateGrouping.selectAllExceptRecommended(groups)
    }

    fun clearSelection() {
        _selectedUris.value = DuplicateGrouping.clearSelection()
    }

    fun deleteSelected() {
        if (selectedUris.isEmpty() || isDeleting) return

        val uris = selectedUris.toList()
        _isDeleting.value = true
        fileControllerEventEmitter.deleteFiles(uris) { result ->
            componentScope.launch {
                handleDeletionResult(result)
            }
        }
    }

    private fun handleDeletionResult(result: FileDeletionResult) {
        val deletedUris = result.deletedUris.toSet()
        if (deletedUris.isNotEmpty()) {
            _sourceUris.update { uris ->
                uris.filterNot { it.toString() in deletedUris }
            }
            analysisResult?.let { current ->
                val remainingItems = current.items.filterNot { it.uri in deletedUris }
                val regrouped = DuplicateGrouping.regroup(remainingItems, sensitivity)
                _analysisResult.value = current.copy(
                    items = remainingItems,
                    groups = regrouped
                )
                updateGroups(regrouped)
            }
        }

        val availableUris = groups
            .flatMapTo(mutableSetOf()) { group -> group.items.map { it.uri } }
        _selectedUris.value = result.failedUris.filterTo(mutableSetOf()) { it in availableUris }
        _isDeleting.value = false
    }

    private fun updateGroups(groups: List<DuplicateGroup>) {
        _groups.value = groups
        val availableUris = groups
            .flatMapTo(mutableSetOf()) { group -> group.items.map { it.uri } }
        _selectedUris.update { it.intersect(availableUris) }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            initialUris: List<Uri>?,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit
        ): DuplicateFinderComponent
    }
}
