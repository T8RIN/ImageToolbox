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

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DrawHistoryCache<T>(
    private val width: Int,
    private val height: Int,
    private val background: Int
) {
    private data class Entry<T>(val paths: List<T>, val bitmap: Bitmap)

    private val mutex = Mutex()
    private val entries = ArrayList<Entry<T>>()

    suspend fun render(
        paths: List<T>,
        draw: suspend (Canvas, Bitmap, T) -> Unit
    ): Bitmap = mutex.withLock {
        val prefix = entries.filter { entry ->
            entry.paths.size <= paths.size && paths.subList(0, entry.paths.size) == entry.paths
        }.maxByOrNull { it.paths.size }
        if (prefix != null && prefix.paths == paths) return@withLock prefix.bitmap

        val bitmap = prefix?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
            ?: createBitmap(width, height)
                .apply { eraseColor(background) }
        try {
            val canvas = Canvas(bitmap)
            for (index in (prefix?.paths?.size ?: 0) until paths.size) {
                currentCoroutineContext().ensureActive()
                draw(canvas, bitmap, paths[index])
            }
            currentCoroutineContext().ensureActive()
            entries += Entry(paths.toList(), bitmap)
            while (entries.size > 1 && entries.sumOf { it.bitmap.allocationByteCount.toLong() } > 32L * 1024 * 1024) {
                entries.removeAt(0)
            }
            while (entries.size > 8) entries.removeAt(0)
            bitmap
        } catch (error: Throwable) {
            bitmap.recycle()
            throw error
        }
    }
}