/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.scaleToFitCanvas
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.feature.draw.domain.PathPaint

@Composable
fun PathPaintPreview(
    modifier: Modifier = Modifier,
    pathPaints: List<PathPaint<Path, Color>>
) {
    val visuals = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant(),
            shape = ShapeDefaults.extraSmall
        )
        .clip(ShapeDefaults.extraSmall)
        .transparencyChecker(
            checkerHeight = 2.dp,
            checkerWidth = 2.dp
        )
    val first = pathPaints.firstOrNull()
    if (first != null) {
        Box(
            modifier = modifier
                .aspectRatio(first.canvasSize.aspectRatio)
                .then(visuals)
                .alpha(0.99f)
                .drawBehind {
                    val currentSize = IntegerSize(
                        size.width.toInt(),
                        size.height.toInt()
                    )
                    drawIntoCanvas { composeCanvas ->
                        val canvas = composeCanvas.nativeCanvas
                        pathPaints.forEach { pathPaint ->
                            val stroke = pathPaint
                                .strokeWidth
                                .toPx(currentSize)

                            val drawPathMode = pathPaint.drawPathMode

                            val isSharpEdge = drawPathMode.isSharpEdge
                            val isFilled = drawPathMode.isFilled

                            canvas.drawPath(
                                pathPaint.path
                                    .scaleToFitCanvas(
                                        currentSize = currentSize,
                                        oldSize = pathPaint.canvasSize
                                    )
                                    .asAndroidPath(),
                                Paint()
                                    .apply {
                                        if (pathPaint.isErasing) {
                                            blendMode = BlendMode.Clear
                                            style = PaintingStyle.Stroke
                                            strokeWidth = stroke
                                            strokeCap = StrokeCap.Round
                                            strokeJoin = StrokeJoin.Round
                                        } else {
                                            color = pathPaint.drawColor
                                            if (isFilled) {
                                                style = PaintingStyle.Fill
                                            } else {
                                                style = PaintingStyle.Stroke
                                                strokeWidth = stroke
                                                if (isSharpEdge) {
                                                    style = PaintingStyle.Stroke
                                                } else {
                                                    strokeCap = StrokeCap.Round
                                                    strokeJoin = StrokeJoin.Round
                                                }
                                            }
                                        }
                                    }
                                    .asFrameworkPaint()
                                    .apply {
                                        if (pathPaint.brushSoftness.value > 0f) {
                                            maskFilter = BlurMaskFilter(
                                                pathPaint.brushSoftness.toPx(currentSize),
                                                BlurMaskFilter.Blur.NORMAL
                                            )
                                        }
                                    }
                            )
                        }
                    }
                }
        )
    } else {
        val color = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.6f)
        val textMeasurer = rememberTextMeasurer()
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .then(visuals)
                .drawBehind {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "N",
                        topLeft = Offset(
                            size.width / 2,
                            size.height / 2
                        ),
                        style = TextStyle(color = color)
                    )
                }
        )
    }
}