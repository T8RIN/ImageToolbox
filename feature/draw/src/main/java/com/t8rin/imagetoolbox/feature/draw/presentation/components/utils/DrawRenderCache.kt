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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.imagetoolbox.feature.draw.presentation.components.drawCommittedPath
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DrawRenderCache {
    private data class Source(val key: Any, val generation: Int?, val background: Int)

    private var source: Source? = null
    private var session: Session? = null

    @Synchronized
    internal fun sessionFor(sourceKey: Any, background: Int): Session {
        val nextSource = Source(sourceKey, (sourceKey as? Bitmap)?.generationId, background)
        if (source != nextSource) {
            source = nextSource
            session = null
        }
        return session ?: Session(background).also { session = it }
    }

    @Synchronized
    fun clear() {
        source = null
        session = null
    }

    internal class Session(private val background: Int) {
        private val mutex = Mutex()
        private var size: IntegerSize? = null
        private var history: DrawHistoryCache<UiPathPaint>? = null
        private val healedPaths = mutableMapOf<List<UiPathPaint>, Bitmap>()

        suspend fun render(
            paths: List<UiPathPaint>,
            canvasSize: IntegerSize,
            source: Bitmap,
            context: Context,
            onRequestFiltering: suspend (Bitmap, List<Filter<*>>) -> Bitmap?,
            preparedPath: UiPathPaint? = null,
            preparedEffect: Bitmap? = null,
            onRenderingPath: (UiPathPaint) -> Unit = {}
        ): Bitmap = mutex.withLock {
            if (size != canvasSize) {
                size = canvasSize
                history = DrawHistoryCache(canvasSize.width, canvasSize.height, background)
            }
            checkNotNull(history).render(paths) { target, bitmap, entry, index ->
                val healKey = if (entry.drawMode is DrawMode.SpotHeal && !entry.isErasing) {
                    paths.take(index + 1)
                } else null
                val cachedEffect = healKey?.let(healedPaths::get)
                val prepared = cachedEffect ?: preparedEffect?.takeIf {
                    entry === preparedPath && it.width == canvasSize.width && it.height == canvasSize.height
                }
                if (cachedEffect == null) onRenderingPath(entry)
                target.drawCommittedPath(
                    uiPathPaint = entry,
                    canvasSize = canvasSize,
                    context = context,
                    source = {
                        source.asImageBitmap().overlay(bitmap.asImageBitmap()).asAndroidBitmap()
                    },
                    onRequestFiltering = { input, filters ->
                        onRequestFiltering(input, filters)?.also {
                            currentCoroutineContext().ensureActive()
                            if (healKey != null) healedPaths[healKey] = it
                        }
                    },
                    preparedEffect = prepared
                )
            }
        }
    }
}