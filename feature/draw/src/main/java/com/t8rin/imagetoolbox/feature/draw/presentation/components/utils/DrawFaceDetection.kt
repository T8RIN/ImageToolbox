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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.UiPathPaint
import com.t8rin.neural_tools.faces.UltraFaceDetector
import com.t8rin.opencv_tools.face.YuNetFaceDetector
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend fun Bitmap.detectFaceRegions(
    drawMode: DrawMode,
    useYuNet: Boolean
): UiPathPaint? {
    val boxes = if (useYuNet) {
        YuNetFaceDetector.detect(this)
    } else {
        UltraFaceDetector().use { detector ->
            detector.detect(this).map { it.bounds }
        }
    }
    currentCoroutineContext().ensureActive()
    if (boxes.isEmpty()) return null

    return UiPathPaint(
        path = Path().apply {
            boxes.forEach { box ->
                val marginX = box.width() * 0.15f
                val marginY = box.height() * 0.2f
                addOval(
                    Rect(
                        left = (box.left - marginX).coerceAtLeast(0f) * width,
                        top = (box.top - marginY).coerceAtLeast(0f) * height,
                        right = (box.right + marginX).coerceAtMost(1f) * width,
                        bottom = (box.bottom + marginY).coerceAtMost(1f) * height
                    )
                )
            }
        },
        strokeWidth = 1.pt,
        brushSoftness = 0.pt,
        isErasing = false,
        drawMode = drawMode,
        canvasSize = IntegerSize(width, height),
        drawPathMode = DrawPathMode.Oval
    )
}