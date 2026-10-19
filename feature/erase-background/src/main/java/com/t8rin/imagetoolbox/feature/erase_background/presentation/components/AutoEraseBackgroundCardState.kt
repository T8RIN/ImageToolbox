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

package com.t8rin.imagetoolbox.feature.erase_background.presentation.components

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.domain.utils.Flavor
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.domain.utils.throttleLatest
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.BgModelType
import com.t8rin.neural_tools.DownloadProgress
import com.t8rin.neural_tools.bgremover.BgRemover
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class AutoEraseBackgroundCardState internal constructor(
    private val scope: CoroutineScope
) {
    val models: List<BgModelType> = BgModelType.entries.let {
        if (Flavor.isFoss()) it.toggle(BgModelType.MlKit) else it
    }

    var selectedModel by mutableStateOf(BgModelType.Default)
        private set

    var downloadedModels by mutableStateOf(setOf(BgModelType.U2NetP, BgModelType.MlKit))
        private set

    private val _downloadProgresses = mutableStateMapOf<BgModelType, DownloadProgress>()
    val downloadProgresses: Map<BgModelType, DownloadProgress>
        get() = _downloadProgresses

    private var downloadJob by mutableStateOf<Job?>(null)
    private var checkJob: Job? = null

    val isDownloading: Boolean
        get() = downloadJob != null

    init {
        scope.launch {
            BgRemover.downloadedModels.collect {
                downloadedModels = withContext(Dispatchers.IO) { readyModels() }
            }
        }
    }

    fun checkModels() {
        if (checkJob != null) return

        checkJob = scope.launch {
            try {
                models.forEach { type ->
                    withContext(Dispatchers.IO) {
                        BgRemover.getRemover(type.toLib()).checkModel()
                    }
                    delay(100)
                }
                downloadedModels = withContext(Dispatchers.IO) { readyModels() }
                if (!isDownloading && selectedModel !in downloadedModels) {
                    selectedModel = BgModelType.Default
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppToastHost.showFailureToast(e)
            } finally {
                checkJob = null
            }
        }
    }

    fun selectModel(type: BgModelType) {
        if (isDownloading) return

        if (selectedModel != type) {
            BgRemover.getRemover(selectedModel.toLib()).close()
        }
        selectedModel = type
        if (type in downloadedModels) return

        _downloadProgresses[type] = DownloadProgress(
            currentPercent = 0f,
            currentTotalSize = 0L
        )
        downloadJob = scope.launch(start = CoroutineStart.LAZY) {
            try {
                BgRemover.downloadModel(type.toLib())
                    .throttleLatest(50)
                    .collect { _downloadProgresses[type] = it }
                downloadedModels = withContext(Dispatchers.IO) {
                    check(BgRemover.getRemover(type.toLib()).checkModel())
                    readyModels()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                selectedModel = BgModelType.Default
                AppToastHost.showFailureToast(e)
            } finally {
                _downloadProgresses.remove(type)
                downloadJob = null
            }
        }
        downloadJob?.start()
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        _downloadProgresses.remove(selectedModel)
        selectedModel = BgModelType.Default
    }

    private fun readyModels(): Set<BgModelType> = models.filter {
        it == BgModelType.MlKit || BgRemover.getRemover(it.toLib()).isDownloaded.value
    }.toSet()
}

private fun BgModelType.toLib(): BgRemover.Type = when (this) {
    BgModelType.MlKit,
    BgModelType.U2NetP -> BgRemover.Type.U2NetP

    BgModelType.U2Net -> BgRemover.Type.U2Net
    BgModelType.RMBG -> BgRemover.Type.RMBG1_4
    BgModelType.InSPyReNet -> BgRemover.Type.InSPyReNet
    BgModelType.BiRefNetTiny -> BgRemover.Type.BiRefNetTiny
    BgModelType.ISNet -> BgRemover.Type.ISNet
    BgModelType.YOLO -> BgRemover.Type.YOLO
    BgModelType.MODNet -> BgRemover.Type.MODNet
    BgModelType.MobileSAM -> BgRemover.Type.MobileSAM
}