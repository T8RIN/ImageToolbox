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

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.neural_tools.bgremover.MobileSAM
import com.t8rin.neural_tools.withCancellableRunOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Stable
internal class MobileSAMSelectionState(
    val source: Bitmap,
    val previewSource: Bitmap
) {
    var positive by mutableStateOf(true)

    var points by mutableStateOf(emptyList<MobileSAM.Point>())
        private set

    var preview by mutableStateOf(previewSource)
        private set

    var isPrepared by mutableStateOf(false)
        private set

    var canApply by mutableStateOf(false)
        private set

    var isApplying by mutableStateOf(false)
        private set

    private var isSegmenting by mutableStateOf(false)

    val isLoading: Boolean
        get() = !isPrepared || isSegmenting || isApplying

    private val mutex = Mutex()
    private val lifecycleMutex = Mutex()
    private var session: MobileSAM.Session? = null

    suspend fun prepare() = lifecycleMutex.withLock {
        canApply = false
        var created: MobileSAM.Session? = null
        try {
            created = withContext(Dispatchers.Default) {
                withCancellableRunOptions(onCancellation = MobileSAM.Session::close) {
                    MobileSAM.openSession(source, it)
                }
            }
            session = created
            isPrepared = true
            awaitCancellation()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppToastHost.showFailureToast(e)
        } finally {
            isPrepared = false
            canApply = false
            session = null
            withContext(NonCancellable + Dispatchers.Default) {
                mutex.withLock { created?.close() }
            }
        }
    }

    fun addPoint(x: Float, y: Float, positive: Boolean) {
        if (!isPrepared || isApplying) return

        points = points + MobileSAM.Point(x, y, positive)
        invalidatePreview()
    }

    fun undoPoint() {
        if (isApplying) return

        points = points.dropLast(1)
        invalidatePreview()
    }

    private fun invalidatePreview() {
        canApply = false
        isSegmenting = points.any { it.positive }
    }

    suspend fun updatePreview() {
        val current = session ?: return
        val selectedPoints = points
        if (selectedPoints.none { it.positive }) {
            preview = previewSource
            canApply = false
            isSegmenting = false
            return
        }

        isSegmenting = true
        canApply = false
        try {
            val result = withContext(Dispatchers.Default) {
                mutex.withLock {
                    withCancellableRunOptions(onCancellation = Bitmap::recycle) {
                        current.segment(
                            points = selectedPoints,
                            runOptions = it,
                            outputImage = previewSource
                        )
                    }
                }
            }
            if (points == selectedPoints) {
                preview = result
                canApply = true
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppToastHost.showFailureToast(e)
        } finally {
            if (points == selectedPoints) isSegmenting = false
        }
    }

    suspend fun applySelection(): Bitmap? {
        val current = session ?: return null
        if (!canApply || isLoading) return null
        if (previewSource === source) return preview

        val selectedPoints = points
        isApplying = true
        return try {
            withContext(Dispatchers.Default) {
                mutex.withLock {
                    withCancellableRunOptions(onCancellation = Bitmap::recycle) {
                        current.segment(selectedPoints, it)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppToastHost.showFailureToast(e)
            null
        } finally {
            isApplying = false
        }
    }
}