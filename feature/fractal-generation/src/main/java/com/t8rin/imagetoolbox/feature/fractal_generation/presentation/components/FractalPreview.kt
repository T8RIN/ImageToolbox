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

package com.t8rin.imagetoolbox.feature.fractal_generation.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.fractal_generation.presentation.model.FractalPreviewFrame
import kotlin.math.abs

@Composable
fun FractalPreview(
    frame: FractalPreviewFrame?,
    renderAspectRatio: Float,
    isLoading: Boolean,
    isThreeDimensional: Boolean,
    showCameraGestureGuide: Boolean,
    backgroundColor: Color,
    onGestureStart: () -> Unit,
    onGesture: (
        anchorX: Float,
        anchorY: Float,
        panX: Float,
        panY: Float,
        zoomFactor: Float,
        aspectRatio: Float
    ) -> Unit,
    onGestureEnd: () -> Unit,
    onCopyCoordinate: (
        normalizedX: Float,
        normalizedY: Float,
        aspectRatio: Float
    ) -> Unit,
    onFrameDisplayed: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    var temporaryScale by remember { mutableFloatStateOf(1f) }
    var temporaryOffset by remember { mutableStateOf(Offset.Zero) }
    var appliedTargetRevision by remember { mutableIntStateOf(NO_TARGET_REVISION) }
    var gestureGuideStart by remember { mutableStateOf(Offset.Zero) }
    var gestureGuideCurrent by remember { mutableStateOf(Offset.Zero) }
    var isGestureGuideActive by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    val currentOnGestureStart by rememberUpdatedState(onGestureStart)
    val currentOnGesture by rememberUpdatedState(onGesture)
    val currentOnGestureEnd by rememberUpdatedState(onGestureEnd)
    val currentOnCopyCoordinate by rememberUpdatedState(onCopyCoordinate)
    val currentRenderAspectRatio by rememberUpdatedState(renderAspectRatio)
    val currentIsThreeDimensional by rememberUpdatedState(isThreeDimensional)
    val currentShowCameraGestureGuide by rememberUpdatedState(showCameraGestureGuide)

    val targetRevision = frame?.targetRevision ?: NO_TARGET_REVISION
    val shouldResetTransform = frame != null && targetRevision != appliedTargetRevision
    SideEffect {
        if (shouldResetTransform) {
            temporaryScale = 1f
            temporaryOffset = Offset.Zero
            appliedTargetRevision = targetRevision
        }
    }
    val loadingScrimAlpha by animateFloatAsState(
        targetValue = if (isLoading) LOADING_SCRIM_ALPHA else 0f,
        animationSpec = tween(LOADING_ANIMATION_MILLIS),
        label = "FractalPreviewLoadingScrim"
    )
    val drawBitmapBorder = LocalSettingsState.current.drawBitmapBorder

    val shape = ShapeDefaults.extremeSmall
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (drawBitmapBorder) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant(),
                        shape = shape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { position ->
                            if (size.width <= 0 || size.height <= 0) return@detectTapGestures

                            val zoomFactor = DOUBLE_TAP_ZOOM
                            if (!currentIsThreeDimensional) {
                                temporaryOffset = temporaryOffset * zoomFactor +
                                        position * (1f - zoomFactor)
                                temporaryScale *= zoomFactor
                            }

                            val gestureAspectRatio = currentRenderAspectRatio
                                .takeIf { it.isFinite() && it > 0f }
                                ?: 1f
                            currentOnGestureStart()
                            currentOnGesture(
                                position.x / size.width,
                                position.y / size.height,
                                0f,
                                0f,
                                zoomFactor,
                                gestureAspectRatio
                            )
                            currentOnGestureEnd()
                        },
                        onLongPress = { position ->
                            if (currentIsThreeDimensional) return@detectTapGestures
                            if (size.width <= 0 || size.height <= 0) return@detectTapGestures

                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentOnCopyCoordinate(
                                position.x / size.width,
                                position.y / size.height,
                                currentRenderAspectRatio
                                    .takeIf { it.isFinite() && it > 0f }
                                    ?: 1f
                            )
                        }
                    )
                }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val firstDown = awaitFirstDown(requireUnconsumed = false)

                        var totalPan = Offset.Zero
                        var totalZoom = 1f
                        var pastTouchSlop = false
                        var canShowGestureGuide = currentIsThreeDimensional &&
                                currentShowCameraGestureGuide
                        if (canShowGestureGuide) {
                            gestureGuideStart = firstDown.position
                            gestureGuideCurrent = firstDown.position
                        }

                        try {
                            do {
                                val event = awaitPointerEvent()
                                val pressedChanges = event.changes.filter { it.pressed }
                                if (pressedChanges.isEmpty()) break
                                if (pressedChanges.size != 1) {
                                    canShowGestureGuide = false
                                    isGestureGuideActive = false
                                }

                                val panChange = event.calculatePan()
                                val zoomChange = event.calculateZoom()
                                val centroid = event.calculateCentroid(useCurrent = false)

                                totalPan += panChange
                                totalZoom *= zoomChange

                                var appliedPan = panChange
                                var appliedZoom = zoomChange

                                if (!pastTouchSlop) {
                                    val zoomMotion = abs(1f - totalZoom) *
                                            event.calculateCentroidSize(useCurrent = false)
                                    val panMotion = totalPan.getDistance()

                                    if (zoomMotion > viewConfiguration.touchSlop ||
                                        panMotion > viewConfiguration.touchSlop
                                    ) {
                                        pastTouchSlop = true
                                        appliedPan = totalPan
                                        appliedZoom = totalZoom
                                        currentOnGestureStart()
                                    }
                                }

                                if (pastTouchSlop && centroid != Offset.Unspecified) {
                                    if (canShowGestureGuide) {
                                        gestureGuideCurrent = pressedChanges.single().position
                                        isGestureGuideActive = true
                                    }
                                    val safeZoom = appliedZoom
                                        .takeIf { it.isFinite() && it > 0f }
                                        ?: 1f
                                    if (!currentIsThreeDimensional) {
                                        temporaryOffset = temporaryOffset * safeZoom +
                                                centroid * (1f - safeZoom) + appliedPan
                                        temporaryScale = (temporaryScale * safeZoom)
                                            .coerceIn(
                                                MIN_TEMPORARY_SCALE,
                                                MAX_TEMPORARY_SCALE
                                            )
                                    }

                                    val gestureAspectRatio = currentRenderAspectRatio
                                        .takeIf { it.isFinite() && it > 0f }
                                        ?: 1f
                                    currentOnGesture(
                                        centroid.x / size.width,
                                        centroid.y / size.height,
                                        appliedPan.x / size.width,
                                        appliedPan.y / size.height,
                                        safeZoom,
                                        gestureAspectRatio
                                    )

                                    event.changes.forEach { change ->
                                        if (change.positionChanged()) change.consume()
                                    }
                                }
                            } while (true)
                        } finally {
                            isGestureGuideActive = false
                            if (pastTouchSlop) currentOnGestureEnd()
                        }
                    }
                }
        ) {
            frame?.let { targetFrame ->
                val previewBitmap = targetFrame.bitmap
                SideEffect {
                    onFrameDisplayed(previewBitmap)
                }
                if (!previewBitmap.isRecycled) {
                    Picture(
                        model = remember(previewBitmap) {
                            previewBitmap.asImageBitmap()
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val useIdentityTransform = isThreeDimensional ||
                                        shouldResetTransform
                                scaleX = if (useIdentityTransform) 1f else temporaryScale
                                scaleY = if (useIdentityTransform) 1f else temporaryScale
                                translationX = if (useIdentityTransform) {
                                    0f
                                } else {
                                    temporaryOffset.x
                                }
                                translationY = if (useIdentityTransform) {
                                    0f
                                } else {
                                    temporaryOffset.y
                                }
                                transformOrigin = TransformOrigin(0f, 0f)
                            },
                        shape = shape,
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.None,
                        showTransparencyChecker = false
                    )
                }
            }
        }

        if (loadingScrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = loadingScrimAlpha))
            )
        }
        AnimatedVisibility(
            visible = isGestureGuideActive &&
                    showCameraGestureGuide &&
                    isThreeDimensional,
            enter = fadeIn(tween(GESTURE_GUIDE_ANIMATION_MILLIS)),
            exit = fadeOut(tween(GESTURE_GUIDE_ANIMATION_MILLIS)),
            modifier = Modifier.matchParentSize()
        ) {
            FractalCameraGestureGuide(
                startPosition = gestureGuideStart,
                currentPosition = gestureGuideCurrent,
                modifier = Modifier.matchParentSize()
            )
        }
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(tween(LOADING_ANIMATION_MILLIS)) + scaleIn(
                animationSpec = tween(LOADING_ANIMATION_MILLIS),
                initialScale = 0.85f
            ),
            exit = fadeOut(tween(LOADING_ANIMATION_MILLIS)) + scaleOut(
                animationSpec = tween(LOADING_ANIMATION_MILLIS),
                targetScale = 0.85f
            )
        ) {
            EnhancedLoadingIndicator()
        }
    }
}

private const val DOUBLE_TAP_ZOOM = 2f
private const val MIN_TEMPORARY_SCALE = 0.01f
private const val MAX_TEMPORARY_SCALE = 100f
private const val LOADING_ANIMATION_MILLIS = 160
private const val GESTURE_GUIDE_ANIMATION_MILLIS = 120
private const val LOADING_SCRIM_ALPHA = 0.35f
private const val NO_TARGET_REVISION = -1
