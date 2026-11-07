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

package com.t8rin.collages.components.preview

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.LruCache
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.t8rin.collages.model.CollageLayout

@Composable
internal fun CollagePreview(
    shapes: List<CollagePreviewShape>,
    color: Color,
    modifier: Modifier
) {
    val background = remember(color) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color.toArgb() }
    }
    val cutout = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }
    Canvas(modifier) {
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            val layer = nativeCanvas.saveLayer(0f, 0f, size.width, size.height, null)
            nativeCanvas.drawRect(0f, 0f, size.width, size.height, background)
            nativeCanvas.scale(size.width / CollagePreviewSize, size.height / CollagePreviewSize)
            shapes.forEach { shape ->
                shape.outline?.let { nativeCanvas.drawPath(it, background) }
                nativeCanvas.drawPath(shape.path, cutout)
            }
            nativeCanvas.restoreToCount(layer)
        }
    }
}

internal object CollagePreviewCache {
    private val shapes = LruCache<String, List<CollagePreviewShape>>(192)

    fun peek(layout: CollageLayout): List<CollagePreviewShape>? = synchronized(shapes) {
        shapes[layout.title]
    }

    fun get(layout: CollageLayout): List<CollagePreviewShape> = peek(layout)
        ?: createCollagePreviewShapes(layout).also {
            synchronized(shapes) { shapes.put(layout.title, it) }
        }
}