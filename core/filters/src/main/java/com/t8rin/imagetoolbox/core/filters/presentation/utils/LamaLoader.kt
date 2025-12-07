/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.presentation.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResourcesDownloadProgress
import com.t8rin.neural_tools.inpaint.LaMaProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

interface LamaLoader {
    val isDownloaded: Boolean
    fun download(): Flow<RemoteResourcesDownloadProgress>

    companion object Companion : LamaLoader by LamaLoaderImpl
}

private object LamaLoaderImpl : LamaLoader {
    private val _isDownloaded: MutableState<Boolean> =
        mutableStateOf(LaMaProcessor.isDownloaded.value)
    override val isDownloaded: Boolean by _isDownloaded

    init {
        LaMaProcessor.isDownloaded.onEach {
            _isDownloaded.value = it
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    override fun download(): Flow<RemoteResourcesDownloadProgress> =
        LaMaProcessor.startDownload().map {
            RemoteResourcesDownloadProgress(
                currentPercent = it.currentPercent,
                currentTotalSize = it.currentTotalSize,
                itemsCount = 1,
                itemsDownloaded = 0
            )
        }
}