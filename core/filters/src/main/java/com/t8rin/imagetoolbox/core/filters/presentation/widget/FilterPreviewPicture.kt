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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer

@Composable
internal fun FilterPreviewPicture(
    model: Any?,
    canShowImage: Boolean,
    canOpenPreview: Boolean,
    onOpenPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (canShowImage) {
            Picture(
                model = model,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .scale(1.2f),
                shape = MaterialTheme.shapes.medium
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(48.dp)
                    .scale(1.2f)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmer(true)
            )
        }
        if (canOpenPreview) {
            val previewContentDescription = stringResource(R.string.image_preview)
            val white = MaterialTheme.colorScheme.secondaryFixed
            val black = MaterialTheme.colorScheme.onSecondaryFixed

            Canvas(
                modifier = Modifier
                    .size(36.dp)
                    .clip(ShapeDefaults.small)
                    .hapticsClickable(onClick = onOpenPreview)
                    .semantics {
                        contentDescription = previewContentDescription
                    }
                    .padding(4.dp)
            ) {
                val strokeWidth = 2.dp.toPx()
                val stroke = Stroke(
                    width = strokeWidth
                )
                val dashedStroke = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            3.dp.toPx(),
                            3.dp.toPx()
                        )
                    )
                )
                val cornerRadius = CornerRadius(8.dp.toPx())
                val borderTopLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                val borderSize = Size(
                    width = size.width - strokeWidth,
                    height = size.height - strokeWidth
                )

                drawRoundRect(
                    color = black,
                    topLeft = borderTopLeft,
                    size = borderSize,
                    cornerRadius = cornerRadius,
                    style = stroke
                )
                drawRoundRect(
                    color = black.copy(0.3f),
                    topLeft = borderTopLeft,
                    size = borderSize,
                    cornerRadius = cornerRadius
                )
                drawRoundRect(
                    color = white,
                    topLeft = borderTopLeft,
                    size = borderSize,
                    cornerRadius = cornerRadius,
                    style = dashedStroke
                )

                val arrowWidth = size.width * 0.3f
                val arrowBorderWidth = strokeWidth * 1.5f

                withTransform(
                    transformBlock = {
                        translate(
                            left = size.width / 2f,
                            top = size.height / 2f
                        )
                        scale(
                            scaleX = arrowWidth,
                            scaleY = arrowWidth,
                            pivot = Offset.Zero
                        )
                    }
                ) {
                    drawPath(
                        path = PlayArrowPath,
                        color = black,
                        style = Stroke(
                            width = arrowBorderWidth / arrowWidth,
                            join = StrokeJoin.Round
                        )
                    )
                    drawPath(
                        path = PlayArrowPath,
                        color = white
                    )
                }
            }
        }
    }
}

private val PlayArrowPath: Path by lazy(LazyThreadSafetyMode.NONE) {
    fun y(value: Float) = value * 1.1666666f

    Path().apply {
        moveTo(-0.32f, y(0.47f))
        lineTo(0.45f, y(0.08f))
        quadraticTo(0.5f, y(0.05f), 0.5f, y(0f))
        quadraticTo(0.5f, y(-0.05f), 0.45f, y(-0.08f))
        lineTo(-0.32f, y(-0.47f))
        quadraticTo(-0.38f, y(-0.5f), -0.44f, y(-0.47f))
        quadraticTo(-0.5f, y(-0.45f), -0.5f, y(-0.39f))
        lineTo(-0.5f, y(0.39f))
        quadraticTo(-0.5f, y(0.45f), -0.44f, y(0.47f))
        quadraticTo(-0.38f, y(0.5f), -0.32f, y(0.47f))
        close()
    }
}