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

package com.t8rin.imagetoolbox.image_cutting.presentation.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.Black
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberAnimatedBorderPhase
import com.t8rin.imagetoolbox.image_cutting.domain.CutParams
import com.t8rin.imagetoolbox.image_cutting.domain.PivotPair
import kotlin.math.abs
import kotlin.math.hypot

@Composable
internal fun CutPreview(
    uri: Uri?,
    params: CutParams,
    modifier: Modifier = Modifier,
    isLoadingFromDifferentPlace: Boolean = false
) {
    var aspectRatio by rememberSaveable(uri) {
        mutableFloatStateOf(1f)
    }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Picture(
            model = uri,
            size = 1500,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            onSuccess = {
                aspectRatio = it.result.image.toBitmap().safeAspectRatio
            },
            shape = RectangleShape,
            isLoadingFromDifferentPlace = isLoadingFromDifferentPlace
        )

        CutFrameBorder(
            modifier = Modifier.fillMaxSize(),
            params = params
        )
    }
}

@Composable
private fun CutFrameBorder(
    modifier: Modifier = Modifier,
    params: CutParams
) {
    val keptRects = remember(params) {
        params.toPreviewRects()
    } ?: return
    val meaningfulEdgeGroups = remember(keptRects) {
        keptRects.map(Rect::toMeaningfulEdges).filter(List<PreviewEdge>::isNotEmpty)
    }

    val isNightMode = LocalSettingsState.current.isNightMode
    val colorScheme = MaterialTheme.colorScheme
    val overlayColor = Black.copy(alpha = if (isNightMode) 0.5f else 0.3f)
    val animatedBorderPhase = rememberAnimatedBorderPhase()

    Canvas(
        modifier = modifier.graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
    ) {
        val strokeWidth = 1.5.dp.toPx()

        drawRect(
            color = overlayColor,
            size = size
        )

        keptRects.forEach { rect ->
            val topLeft = Offset(
                x = rect.left * size.width,
                y = rect.top * size.height
            )
            val rectSize = Size(
                width = rect.width * size.width,
                height = rect.height * size.height
            )

            drawRect(
                color = Color.Transparent,
                blendMode = BlendMode.Clear,
                topLeft = topLeft,
                size = rectSize
            )
        }

        meaningfulEdgeGroups.forEach { edges ->
            var accumulatedLength = 0f

            edges.forEach { edge ->
                val start = Offset(
                    x = edge.start.x * size.width,
                    y = edge.start.y * size.height
                )
                val end = Offset(
                    x = edge.end.x * size.width,
                    y = edge.end.y * size.height
                )
                val lineLength = hypot(end.x - start.x, end.y - start.y)

                drawLine(
                    color = colorScheme.primary,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth
                )

                drawLine(
                    color = colorScheme.primaryContainer,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = DashIntervals,
                        phase = animatedBorderPhase - accumulatedLength
                    )
                )

                accumulatedLength += lineLength
            }
        }
    }
}

private fun CutParams.toPreviewRects(): List<Rect>? {
    val vertical = vertical.toPreviewRangeOrNull()
    val horizontal = horizontal.toPreviewRangeOrNull()

    if (vertical == null && horizontal == null) return null

    val xSegments = vertical.toPreviewSegments(inverse = inverseVertical)
    val ySegments = horizontal.toPreviewSegments(inverse = inverseHorizontal)

    return buildList {
        xSegments.forEach { xRange ->
            ySegments.forEach { yRange ->
                Rect(
                    left = xRange.start,
                    top = yRange.start,
                    right = xRange.endInclusive,
                    bottom = yRange.endInclusive
                ).takeIf {
                    it.width > 0f && it.height > 0f
                }?.let(::add)
            }
        }
    }
}

private fun PivotPair?.toPreviewRangeOrNull(): ClosedFloatingPointRange<Float>? {
    return this
        ?.takeIf { it != PivotPair(0f, 1f) }
        ?.let {
            it.startRtlAdjusted.coerceIn(0f, 1f)..it.endRtlAdjusted.coerceIn(0f, 1f)
        }
}

private fun ClosedFloatingPointRange<Float>?.toPreviewSegments(
    inverse: Boolean
): List<ClosedFloatingPointRange<Float>> {
    return when {
        this == null -> listOf(0f..1f)
        inverse -> listOf(this)
        else -> buildList {
            if (start > 0f) add(0f..start)
            if (endInclusive < 1f) add(endInclusive..1f)
        }
    }
}

private data class PreviewEdge(
    val start: Offset,
    val end: Offset
)

private val DashIntervals = floatArrayOf(20f, 20f)

private fun Rect.toMeaningfulEdges(): List<PreviewEdge> {
    return buildList {
        if (!top.isCloseTo(0f)) {
            add(
                PreviewEdge(
                    start = Offset(left, top),
                    end = Offset(right, top)
                )
            )
        }
        if (!right.isCloseTo(1f)) {
            add(
                PreviewEdge(
                    start = Offset(right, top),
                    end = Offset(right, bottom)
                )
            )
        }
        if (!bottom.isCloseTo(1f)) {
            add(
                PreviewEdge(
                    start = Offset(right, bottom),
                    end = Offset(left, bottom)
                )
            )
        }
        if (!left.isCloseTo(0f)) {
            add(
                PreviewEdge(
                    start = Offset(left, bottom),
                    end = Offset(left, top)
                )
            )
        }
    }
}

private fun Float.isCloseTo(
    other: Float,
    epsilon: Float = 0.001f
): Boolean = abs(this - other) <= epsilon

@EnPreview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(false) {
    CutPreview(
        uri = "111".toUri(),
        params = CutParams(
            vertical = PivotPair(start = 0.2f, end = 0.8f),
            horizontal = PivotPair(start = 0.3f, end = 0.65f),
            inverseVertical = false,
            inverseHorizontal = true
        )
    )
}
