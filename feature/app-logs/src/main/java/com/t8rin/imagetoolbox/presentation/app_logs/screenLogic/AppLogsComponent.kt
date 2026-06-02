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

package com.t8rin.imagetoolbox.presentation.app_logs.screenLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.utils.LogLineReference
import com.t8rin.imagetoolbox.core.utils.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AppLogsComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val shareProvider: ShareProvider,
    private val settingsManager: SettingsManager
) : BaseComponent(dispatchersHolder, componentContext) {

    private val allLines = mutableStateListOf<LogLineReference>()
    private val matchingLines = mutableStateListOf<LogLineReference>()

    private val _searchQuery = mutableStateOf("")
    val searchQuery by _searchQuery

    private val _isRefreshing = mutableStateOf(true)
    val isRefreshing by _isRefreshing

    private val _isSearchLoading = mutableStateOf(false)
    val isSearchLoading by _isSearchLoading

    private val _isSendingLogs = mutableStateOf(false)
    val isSendingLogs by _isSendingLogs

    private var refreshJob by smartJob()
    private var searchJob by smartJob()
    private var indexedOffset = 0L
    private var indexedLineNumber = 0
    private var searchIndexedOffset = 0L
    private var searchIndexedLineNumber = 0

    val linesCount: Int
        get() = activeLines.size

    private val activeLines: List<LogLineReference>
        get() = if (searchQuery.isBlank()) allLines else matchingLines

    init {
        refreshLogs()
        observeLogs()
    }

    fun refreshLogs() {
        refreshJob = componentScope.launch {
            syncLogs(
                forceRebuild = true,
                showLoading = true
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
        searchJob = componentScope.launch {
            _isSearchLoading.update { true }
            delay(400)
            if (query.isBlank()) {
                matchingLines.clear()
                searchIndexedOffset = indexedOffset
                searchIndexedLineNumber = indexedLineNumber
                _isSearchLoading.update { false }
                return@launch
            }

            try {
                syncSearch(
                    query = query,
                    forceRebuild = true
                )
            } finally {
                if (query == searchQuery) {
                    _isSearchLoading.update { false }
                }
            }
        }
    }

    fun lineAtOrNull(index: Int): LogLineReference? {
        val lines = activeLines
        return lines.getOrNull(lines.lastIndex - index)
    }

    fun lineKey(index: Int): String = lineAtOrNull(index)?.key ?: index.toString()

    fun shareLogs() {
        componentScope.launch {
            _isSendingLogs.update { true }
            runSuspendCatching {
                shareProvider.shareUri(
                    uri = settingsManager.createLogsExport(),
                    onComplete = {}
                )
            }
            _isSendingLogs.update { false }
        }
    }

    private fun observeLogs() {
        componentScope.launch {
            while (true) {
                delay(LOG_REFRESH_INTERVAL)
                runCatching {
                    syncLogs(
                        forceRebuild = false,
                        showLoading = false
                    )
                }
            }
        }
    }

    private suspend fun syncLogs(
        forceRebuild: Boolean,
        showLoading: Boolean
    ) {
        if (showLoading) _isRefreshing.update { true }

        try {
            val currentLength = withContext(ioDispatcher) {
                Logger.logsFileLength()
            }
            val shouldRebuild = forceRebuild || currentLength < indexedOffset

            if (!shouldRebuild && currentLength == indexedOffset) return

            val snapshot = withContext(ioDispatcher) {
                Logger.readLogLineReferences(
                    startOffset = if (shouldRebuild) 0L else indexedOffset,
                    startLineNumber = if (shouldRebuild) 0 else indexedLineNumber
                )
            }

            if (shouldRebuild) allLines.clear()
            allLines.addAll(snapshot.lines)
            indexedOffset = snapshot.endOffset
            indexedLineNumber = snapshot.lastLineNumber

            searchQuery.takeIf(String::isNotBlank)?.let { query ->
                syncSearch(
                    query = query,
                    forceRebuild = shouldRebuild || searchIndexedOffset > indexedOffset
                )
            }
        } finally {
            if (showLoading) {
                _isRefreshing.update { false }
            }
        }
    }

    private suspend fun syncSearch(
        query: String,
        forceRebuild: Boolean
    ) {
        val shouldRebuild = forceRebuild || searchIndexedOffset > indexedOffset
        val snapshot = withContext(ioDispatcher) {
            Logger.readLogLineReferences(
                startOffset = if (shouldRebuild) 0L else searchIndexedOffset,
                startLineNumber = if (shouldRebuild) 0 else searchIndexedLineNumber,
                query = query
            )
        }

        if (query != searchQuery) return

        if (shouldRebuild) matchingLines.clear()
        matchingLines.addAll(snapshot.lines)
        searchIndexedOffset = snapshot.endOffset
        searchIndexedLineNumber = snapshot.lastLineNumber
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit
        ): AppLogsComponent
    }

    private companion object {
        const val LOG_REFRESH_INTERVAL = 2_000L
    }

}