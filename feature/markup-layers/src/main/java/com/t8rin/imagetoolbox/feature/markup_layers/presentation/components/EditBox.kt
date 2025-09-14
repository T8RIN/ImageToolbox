/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlinx.coroutines.launch

@Composable
fun BoxWithConstraintsScope.EditBox(
    state: EditBoxState,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    onLongTap: (() -> Unit)? = null,
    shape: Shape = ShapeDefaults.extraSmall,
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
        shape = shape,
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
    shape: Shape = ShapeDefaults.extraSmall,
    content: @Composable BoxScope.() -> Unit
) {
    if (!state.isVisible) return

    var contentSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val parentMaxWidth = parentSize.width
    val parentMaxHeight = parentSize.height

    SideEffect {
        state.canvasSize = parentSize
    }

    val transformState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        state.applyChanges(
            parentMaxWidth = parentMaxWidth,
            parentMaxHeight = parentMaxHeight,
            contentSize = contentSize,
            zoomChange = zoomChange,
            offsetChange = offsetChange,
            rotationChange = rotationChange
        )
    }

    var needRecalculations by rememberSaveable(state.coerceToBounds, contentSize) {
        mutableStateOf(state.coerceToBounds && contentSize != IntSize.Zero)
    }

    LaunchedEffect(needRecalculations) {
        if (needRecalculations) {
            state.applyChanges(
                parentMaxWidth = parentMaxWidth,
                parentMaxHeight = parentMaxHeight,
                contentSize = contentSize,
                zoomChange = 1f,
                offsetChange = Offset.Zero,
                rotationChange = 0f
            )
            needRecalculations = false
        }
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
    Box(
        modifier = modifier
            .onSizeChanged {
                contentSize = it
            }
            .graphicsLayer(
                scaleX = state.scale,
                scaleY = state.scale,
                rotationZ = state.rotation,
                translationX = state.offset.x,
                translationY = state.offset.y
            )
            .scale(tapScale.value)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary.copy(0.2f * borderAlpha))
            .pointerInput(onTap, animateTap) {
                detectTapGestures(
                    onLongPress = onLongTap?.let {
                        {
                            it()
                            animateTap()
                        }
                    }
                ) {
                    onTap()
                    if (state.isActive) animateTap()
                }
            }
            .then(
                if (state.isActive) {
                    Modifier.transformable(
                        state = transformState
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.alpha(state.alpha)) {
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

    if (state.isActive) {
        val globalTransformState = rememberTransformableState { zoomChange, _, rotationChange ->
            state.applyChanges(
                parentMaxWidth = parentMaxWidth,
                parentMaxHeight = parentMaxHeight,
                contentSize = contentSize,
                zoomChange = zoomChange,
                offsetChange = Offset.Zero,
                rotationChange = rotationChange
            )
        }

        Box(
            Modifier
                .zIndex(-100f)
                .fillMaxSize()
                .transformable(
                    state = globalTransformState,
                    enabled = state.isActive
                )
        )
    }
}

@Composable
internal fun AnimatedBorder(
    modifier: Modifier,
    alpha: Float,
    scale: Float,
    shape: Shape
) {
    val transition: InfiniteTransition = rememberInfiniteTransition()

    // Infinite phase animation for PathEffect
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(20f, 20f),
        phase = phase
    )

    val density = LocalDensity.current
    val colorScheme = MaterialTheme.colorScheme
    Canvas(modifier = modifier) {
        val outline = shape.createOutline(
            size = size,
            layoutDirection = layoutDirection,
            density = density
        )
        drawOutline(
            outline = outline,
            color = colorScheme.primary.copy(alpha),
            style = Stroke(
                width = 3.dp.toPx() * (1f / scale)
            )
        )
        drawOutline(
            outline = outline,
            color = colorScheme.primaryContainer.copy(alpha),
            style = Stroke(
                width = 3.dp.toPx() * (1f / scale),
                pathEffect = pathEffect
            )
        )
    }
}