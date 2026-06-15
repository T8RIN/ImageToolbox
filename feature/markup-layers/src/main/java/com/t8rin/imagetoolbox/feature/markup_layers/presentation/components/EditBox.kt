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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntSize
import com.t8rin.imagetoolbox.core.data.image.utils.toPaint
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.other.AnimatedBorder
import kotlinx.coroutines.launch

@Composable
fun BoxWithConstraintsScope.EditBox(
    state: EditBoxState,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    onLongTap: (() -> Unit)? = null,
    cornerRadiusPercent: Int = 0,
    blendingMode: BlendingMode = BlendingMode.SrcOver,
    isInteractive: Boolean = true,
    animateOnTapWhenInactive: Boolean = false,
    showSelectionBackground: Boolean = true,
    adjustScaleOnCanvasResize: Boolean = true,
    preserveBoundsOnCanvasResize: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val parentSize by remember(constraints) {
        derivedStateOf {
            IntegerSize(
                constraints.maxWidth,
                constraints.maxHeight
            )
        }
    }
    EditBox(
        modifier = modifier,
        onTap = onTap,
        onLongTap = onLongTap,
        state = state,
        parentSize = parentSize,
        cornerRadiusPercent = cornerRadiusPercent,
        blendingMode = blendingMode,
        isInteractive = isInteractive,
        animateOnTapWhenInactive = animateOnTapWhenInactive,
        showSelectionBackground = showSelectionBackground,
        adjustScaleOnCanvasResize = adjustScaleOnCanvasResize,
        preserveBoundsOnCanvasResize = preserveBoundsOnCanvasResize,
        content = content
    )
}

@Composable
fun EditBox(
    state: EditBoxState,
    onTap: () -> Unit,
    parentSize: IntegerSize,
    modifier: Modifier = Modifier,
    onLongTap: (() -> Unit)? = null,
    cornerRadiusPercent: Int = 0,
    blendingMode: BlendingMode = BlendingMode.SrcOver,
    isInteractive: Boolean = true,
    animateOnTapWhenInactive: Boolean = false,
    showSelectionBackground: Boolean = true,
    adjustScaleOnCanvasResize: Boolean = true,
    preserveBoundsOnCanvasResize: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    if (!state.isVisible) return

    var measuredContentGeometry by remember {
        mutableStateOf<MeasuredContentGeometry?>(null)
    }
    val contentSize = measuredContentGeometry?.contentSize ?: IntSize.Zero

    SideEffect {
        if (
            measuredContentGeometry?.canvasSize == parentSize &&
            contentSize != IntSize.Zero
        ) {
            state.syncCanvasGeometry(
                canvasSize = parentSize,
                contentSize = contentSize,
                cornerRadiusPercent = cornerRadiusPercent,
                adjustScale = adjustScaleOnCanvasResize,
                preserveBoundsPosition = preserveBoundsOnCanvasResize
            )
        }
    }

    var lastBoundsRecalculationGeometry by remember {
        mutableStateOf<MeasuredContentGeometry?>(null)
    }
    var skipNextContentBoundsRecalculation by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        state.coerceToBounds,
        measuredContentGeometry,
        cornerRadiusPercent
    ) {
        val geometry = measuredContentGeometry
        if (!state.coerceToBounds || geometry == null || contentSize == IntSize.Zero) return@LaunchedEffect

        val previousGeometry = lastBoundsRecalculationGeometry
        val isCanvasResize = previousGeometry != null &&
                previousGeometry.canvasSize != geometry.canvasSize
        val isContentResize = previousGeometry != null &&
                previousGeometry.contentSize != geometry.contentSize
        val shouldRecalculateBounds = when {
            previousGeometry == null -> true
            isCanvasResize -> false
            isContentResize && skipNextContentBoundsRecalculation -> false
            isContentResize -> true
            else -> false
        }

        if (shouldRecalculateBounds) {
            state.applyChanges(
                parentMaxWidth = geometry.canvasSize.width,
                parentMaxHeight = geometry.canvasSize.height,
                contentSize = geometry.contentSize,
                cornerRadiusPercent = cornerRadiusPercent,
                zoomChange = 1f,
                offsetChange = Offset.Zero,
                rotationChange = 0f
            )
        }
        skipNextContentBoundsRecalculation = isCanvasResize
        lastBoundsRecalculationGeometry = geometry
    }

    val tapScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val animateTap = {
        haptics.longPress()
        scope.launch {
            tapScale.animateTo(0.98f)
            tapScale.animateTo(1.02f)
            tapScale.animateTo(1f)
        }
    }

    val borderAlpha by animateFloatAsState(if (state.isActive) 1f else 0f)
    val shape = AutoCornersShape(
        animateIntAsState(cornerRadiusPercent).value
    )
    val selectionBackgroundColor = MaterialTheme.colorScheme.primary.copy(
        alpha = 0.2f * borderAlpha
    )
    val interactionModifier = if (isInteractive) {
        Modifier.pointerInput(onTap, animateTap) {
            detectTapGestures(
                onLongPress = onLongTap?.let {
                    {
                        it()
                        animateTap()
                    }
                }
            ) {
                onTap()
                if (state.isActive || animateOnTapWhenInactive) animateTap()
            }
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                measuredContentGeometry = MeasuredContentGeometry(
                    canvasSize = parentSize,
                    contentSize = it.size
                )
            }
            .graphicsLayer(
                scaleX = state.scale * if (state.isFlippedHorizontally) -1f else 1f,
                scaleY = state.scale * if (state.isFlippedVertically) -1f else 1f,
                rotationZ = state.rotation,
                translationX = state.offset.x,
                translationY = state.offset.y
            )
            .scale(tapScale.value)
            .clip(shape)
            .drawWithCache {
                onDrawWithContent {
                    if (showSelectionBackground && state.isActive && blendingMode == BlendingMode.SrcOver) {
                        drawRect(selectionBackgroundColor)
                    }
                    drawContent()
                }
            }
            .then(interactionModifier),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .layerBlendingMode(
                    mode = blendingMode,
                    alpha = state.alpha
                )
        ) {
            content()
        }
        AnimatedBorder(
            modifier = Modifier.matchParentSize(),
            alpha = borderAlpha,
            scale = state.scale,
            shape = shape
        )
        if (state.isActive) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.matchParentSize()
            ) { }
        }
    }
}

private data class MeasuredContentGeometry(
    val canvasSize: IntegerSize,
    val contentSize: IntSize
)

private fun Modifier.layerBlendingMode(
    mode: BlendingMode,
    alpha: Float
): Modifier {
    val coercedAlpha = alpha.coerceIn(0f, 1f)

    if (mode == BlendingMode.SrcOver) {
        return if (coercedAlpha >= 0.999f) this else graphicsLayer {
            this.alpha = coercedAlpha
        }
    }

    return drawWithCache {
        val paint = mode.toPaint().asComposePaint().apply {
            this.alpha = coercedAlpha
        }
        onDrawWithContent {
            drawContext.canvas.saveLayer(size.toRect(), paint)
            drawContent()
            drawContext.canvas.restore()
        }
    }
}
