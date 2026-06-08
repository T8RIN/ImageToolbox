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

package com.t8rin.imagetoolbox.core.ui.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

abstract class BaseHistoryComponent<Snapshot>(
    dispatchersHolder: DispatchersHolder,
    componentContext: ComponentContext
) : BaseComponent(dispatchersHolder, componentContext) {

    protected open val maxHistorySize: Int = 25
    protected open val historyTransactionDebounce: Long = 700L
    protected open val formatHistoryTransactionDebounce: Long = 2500L

    private val _history: MutableState<List<Snapshot>> = mutableStateOf(emptyList())
    protected val history: List<Snapshot> by _history

    private val _redoHistory: MutableState<List<Snapshot>> = mutableStateOf(emptyList())
    protected val redoHistory: List<Snapshot> by _redoHistory

    private var pendingHistorySnapshot: Snapshot? = null
    private var pendingHistoryJob: Job? = null
    private var pendingHistoryDelayMillis: Long = historyTransactionDebounce

    private val _hasPendingHistoryTransaction = mutableStateOf(false)

    protected var pendingHistoryMode: PendingHistoryMode? = null
        private set

    val canUndo: Boolean
        get() = history.size > 1 || (_hasPendingHistoryTransaction.value && history.isNotEmpty())

    val canRedo: Boolean
        get() = redoHistory.isNotEmpty()

    fun undo() {
        finalizePendingHistoryTransaction()
        if (!canUndo) return

        val current = history.last()
        val previous = history[history.lastIndex - 1]

        _history.value = history.dropLast(1)
        _redoHistory.value = (redoHistory + current).takeLast(maxHistorySize)
        applyHistorySnapshot(previous)
        registerChanges()
    }

    fun redo() {
        finalizePendingHistoryTransaction()
        if (!canRedo) return

        val snapshot = redoHistory.last()

        _redoHistory.value = redoHistory.dropLast(1)
        _history.value = (history + snapshot).takeLast(maxHistorySize)
        applyHistorySnapshot(snapshot)
        registerChanges()
    }

    protected abstract fun currentHistorySnapshot(): Snapshot

    protected abstract fun applyHistorySnapshot(snapshot: Snapshot)

    protected open fun hasSameUndoState(
        first: Snapshot,
        second: Snapshot
    ): Boolean = first == second

    protected fun restoreBackgroundColorForNoAlphaFormats(
        settingsManager: SettingsManager,
        backgroundColorForNoAlphaFormats: ColorModel
    ) {
        if (
            settingsManager.settingsState.value.backgroundForNoAlphaImageFormats !=
            backgroundColorForNoAlphaFormats
        ) {
            componentScope.launch {
                settingsManager.setBackgroundColorForNoAlphaFormats(
                    color = backgroundColorForNoAlphaFormats
                )
            }
        }
    }

    protected fun commitHistoryFrom(beforeSnapshot: Snapshot) {
        val afterSnapshot = currentHistorySnapshot()
        val hasStateChange = !hasSameUndoState(afterSnapshot, beforeSnapshot)
        val hasHistoryChange = history
            .lastOrNull()
            ?.let { !hasSameUndoState(it, afterSnapshot) } == true

        if (!hasStateChange && !hasHistoryChange) return

        _history.value = history
            .appendHistorySnapshot(beforeSnapshot)
            .appendHistorySnapshot(afterSnapshot)
            .takeLast(maxHistorySize)
        _redoHistory.value = emptyList()
        registerChanges()
    }

    protected fun resetHistory() {
        cancelPendingHistoryTransaction()
        _history.value = listOf(currentHistorySnapshot())
        _redoHistory.value = emptyList()
    }

    protected fun clearHistory() {
        cancelPendingHistoryTransaction()
        _history.value = emptyList()
        _redoHistory.value = emptyList()
    }

    protected fun beginPendingHistoryTransaction(
        mode: PendingHistoryMode? = null,
        commitDelayMillis: Long = historyTransactionDebounce
    ) {
        if (pendingHistorySnapshot == null) {
            pendingHistorySnapshot = currentHistorySnapshot()
            pendingHistoryMode = mode
            _hasPendingHistoryTransaction.value = true
        } else if (mode != null) {
            pendingHistoryMode = mode
        }
        pendingHistoryDelayMillis = commitDelayMillis
    }

    protected fun schedulePendingHistoryCommit() {
        pendingHistoryJob?.cancel()
        pendingHistoryJob = componentScope.launch {
            delay(pendingHistoryDelayMillis)
            val beforeSnapshot = pendingHistorySnapshot
            pendingHistorySnapshot = null
            pendingHistoryJob = null
            pendingHistoryMode = null
            _hasPendingHistoryTransaction.value = false
            beforeSnapshot?.let(::commitHistoryFrom)
        }
    }

    protected fun finalizePendingHistoryTransaction() {
        pendingHistoryJob?.cancel()
        pendingHistoryJob = null
        val beforeSnapshot = pendingHistorySnapshot
        pendingHistorySnapshot = null
        pendingHistoryMode = null
        _hasPendingHistoryTransaction.value = false
        beforeSnapshot?.let(::commitHistoryFrom)
    }

    protected fun cancelPendingHistoryTransaction() {
        pendingHistoryJob?.cancel()
        pendingHistoryJob = null
        pendingHistorySnapshot = null
        pendingHistoryMode = null
        _hasPendingHistoryTransaction.value = false
    }

    private fun List<Snapshot>.appendHistorySnapshot(
        snapshot: Snapshot
    ): List<Snapshot> = when {
        isEmpty() -> listOf(snapshot)
        hasSameUndoState(last(), snapshot) -> dropLast(1) + snapshot
        else -> this + snapshot
    }

    enum class PendingHistoryMode {
        FormatChange
    }
}