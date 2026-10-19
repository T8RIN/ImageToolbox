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
    private val _positive = mutableStateOf(true)
    val positive: Boolean by _positive

    private val _points = mutableStateOf(emptyList<MobileSAM.Point>())
    val points: List<MobileSAM.Point> by _points

    private val _preview = mutableStateOf(previewSource)
    val preview: Bitmap by _preview

    private val _isPrepared = mutableStateOf(false)
    val isPrepared: Boolean by _isPrepared

    private val _canApply = mutableStateOf(false)
    val canApply: Boolean by _canApply

    private val _isApplying = mutableStateOf(false)
    val isApplying: Boolean by _isApplying

    private var isSegmenting by mutableStateOf(false)

    val isLoading: Boolean
        get() = !isPrepared || isSegmenting || isApplying

    private val mutex = Mutex()
    private val lifecycleMutex = Mutex()
    private var session: MobileSAM.Session? = null
    private var previewRequest: Any? = null

    fun setPositive(positive: Boolean) {
        _positive.value = positive
    }

    suspend fun prepare() = lifecycleMutex.withLock {
        _canApply.value = false
        var created: MobileSAM.Session? = null
        try {
            withContext(Dispatchers.Default) {
                created = withCancellableRunOptions(onCancellation = MobileSAM.Session::close) {
                    MobileSAM.openSession(source, it)
                }
            }
            session = created
            _isPrepared.value = true
            awaitCancellation()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppToastHost.showFailureToast(e)
        } finally {
            previewRequest = null
            _isPrepared.value = false
            _canApply.value = false
            session = null
            withContext(NonCancellable + Dispatchers.Default) {
                mutex.withLock { created?.close() }
            }
        }
    }

    fun addPoint(x: Float, y: Float, positive: Boolean) {
        if (!isPrepared || isApplying) return

        _points.value = points + MobileSAM.Point(x, y, positive)
        invalidatePreview()
    }

    fun undoPoint() {
        if (isApplying) return

        _points.value = points.dropLast(1)
        invalidatePreview()
    }

    private fun invalidatePreview() {
        previewRequest = null
        _canApply.value = false
        isSegmenting = points.any { it.positive }
    }

    suspend fun updatePreview() {
        val current = session ?: return
        val selectedPoints = points
        val request = Any().also { previewRequest = it }
        if (selectedPoints.none { it.positive }) {
            _preview.value = previewSource
            _canApply.value = false
            isSegmenting = false
            return
        }

        isSegmenting = true
        _canApply.value = false
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
            if (previewRequest === request) {
                _preview.value = result
                _canApply.value = true
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppToastHost.showFailureToast(e)
        } finally {
            if (previewRequest === request) isSegmenting = false
        }
    }

    suspend fun applySelection(): Bitmap? {
        val current = session ?: return null
        if (!canApply || isLoading) return null
        if (previewSource === source) return preview

        val selectedPoints = points
        _isApplying.value = true
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
            _isApplying.value = false
        }
    }
}