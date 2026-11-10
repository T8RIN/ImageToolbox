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

package com.t8rin.collages.components

import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.t8rin.collages.gesture.CollageTouchDispatcher
import com.t8rin.collages.layout.CollageLayoutStore
import com.t8rin.collages.model.CollageSlotStyle
import com.t8rin.collages.model.CollageState
import com.t8rin.collages.model.CollageType
import com.t8rin.collages.view.FramePhotoLayout
import kotlinx.coroutines.CancellationException
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun Collage(
    images: List<Uri>,
    modifier: Modifier = Modifier,
    spacing: Float = 0f,
    cornerRadius: Float = 0f,
    backgroundColor: Color = Color.White,
    onCollageCreated: (Bitmap) -> Unit,
    onCollageCreationFailed: (Throwable) -> Unit = {},
    collageCreationTrigger: Boolean,
    collageType: CollageType,
    userInteractionEnabled: Boolean = true,
    imageGesturesEnabled: Boolean = true,
    maxImageZoom: Float = 16f,
    canvasScale: Float = 1f,
    outerPadding: Float = 0f,
    slotStyles: List<CollageSlotStyle> = emptyList(),
    slotBorderShaders: List<((Float, Float) -> Shader)?> = emptyList(),
    aspectRatio: Float = 1f,
    outputScaleRatio: Float = 1.5f,
    onImageTap: ((index: Int) -> Unit)? = null,
    handleDrawable: Drawable? = null,
    disableRotation: Boolean = false,
    enableSnapToBorders: Boolean = false,
    backgroundShader: ((Float, Float) -> Shader)? = null,
    state: CollageState? = null,
    onStateReady: ((CollageState) -> Unit)? = null,
    onImagesLoaded: (() -> Unit)? = null,
    onOutputSizeChanged: ((Int, Int) -> Unit)? = null,
    onUserStateChange: ((before: CollageState, after: CollageState, swappedImages: Pair<Int, Int>?) -> Unit)? = null
) {
    require(maxImageZoom.isFinite() && maxImageZoom >= 1f)
    val outlineColor = MaterialTheme.colorScheme.primary.toArgb()
    val outlineAccent = MaterialTheme.colorScheme.primaryContainer.toArgb()
    val layout = remember(collageType.layoutId, collageType.design) {
        collageType.layoutId?.let {
            CollageLayoutStore.getLayout(
                layoutId = it,
                design = collageType.design
            )?.layout
        }
    }
    val imagesLoaded by rememberUpdatedState(onImagesLoaded)
    val stateReady by rememberUpdatedState(onStateReady)
    val stateChanged by rememberUpdatedState(onUserStateChange)
    val imageCreated by rememberUpdatedState(onCollageCreated)
    val creationFailed by rememberUpdatedState(onCollageCreationFailed)
    val outputSizeChanged by rememberUpdatedState(onOutputSizeChanged)
    val ratio = aspectRatio.takeIf { it.isFinite() && it > 0f } ?: 1f

    AnimatedVisibility(
        visible = layout != null,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        key(layout?.title) {
            BoxWithConstraints {
                val width = min(constraints.maxWidth.toFloat(), constraints.maxHeight * ratio)
                    .roundToInt().coerceAtLeast(1)
                val height = (width / ratio).roundToInt().coerceAtLeast(1)
                val decorationScale = min(width, height) / 1000f
                var view by remember { mutableStateOf<FramePhotoLayout?>(null) }
                val applied = remember { AppliedCollageState() }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    AndroidView(
                        modifier = with(LocalDensity.current) {
                            Modifier.size(
                                width.toDp(),
                                height.toDp()
                            )
                        },
                        factory = { context ->
                            FramePhotoLayout(
                                context,
                                layout?.photoItemList.orEmpty(),
                                maxImageZoom
                            ).also {
                                it.setParamsManager(layout?.paramsManager)
                                view = it
                            }
                        },
                        onRelease = { it.release() },
                        update = { target ->
                            val stateToRestore = applied.state.update(
                                state?.takeIf { it.layoutId == layout?.title }
                            )
                            target.onImagesLoaded = { imagesLoaded?.invoke() }
                            target.setStateListeners(
                                layoutId = layout?.title.orEmpty(),
                                onStateReady = {
                                    applied.state.current = it
                                    stateReady?.invoke(it)
                                },
                                onUserStateChange = { before, after, swappedImages ->
                                    stateChanged?.invoke(
                                        before,
                                        after,
                                        swappedImages
                                    )
                                }
                            )
                            target.layoutDesign = layout?.design
                            target.slotStyles = slotStyles
                            target.slotBorderShaders = slotBorderShaders
                            target.outerPadding = outerPadding * decorationScale
                            target.setOutlineStyle(outlineColor, outlineAccent, canvasScale)
                            target.setBackgroundColor(backgroundColor)
                            target.setBackgroundShader(backgroundShader)
                            target.setOnItemTapListener(onImageTap)
                            target.setHandleDrawable(handleDrawable)
                            target.setDisableRotation(disableRotation)
                            target.setEnableSnapToBorders(enableSnapToBorders)
                            target.outputAspectRatio = ratio
                            target.maxImageZoom = maxImageZoom
                            if (applied.images != images) {
                                target.updateImages(images)
                                applied.images = images
                            }
                            if (applied.width == 0) {
                                target.build(
                                    width,
                                    height,
                                    spacing * decorationScale,
                                    cornerRadius * decorationScale
                                )
                            } else if (applied.width != width || applied.height != height) {
                                target.resize(width, height)
                            }
                            applied.width = width
                            applied.height = height
                            target.setSpace(
                                spacing * decorationScale,
                                cornerRadius * decorationScale
                            )
                            stateToRestore?.let {
                                applied.state.current = it
                                target.restoreState(it)
                            }
                            target.onImageStateReady()
                        }
                    )
                }
                if (userInteractionEnabled && imageGesturesEnabled) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .pointerInput(view) {
                                val target = view ?: return@pointerInput
                                val dispatcher = CollageTouchDispatcher(target)
                                awaitEachGesture {
                                    awaitFirstDown(requireUnconsumed = false)
                                    try {
                                        do {
                                            val event = currentEvent
                                            dispatcher.dispatch(event)
                                            event.changes.forEach { it.consume() }
                                            if (event.changes.none { it.pressed }) break
                                            awaitPointerEvent()
                                        } while (true)
                                    } finally {
                                        dispatcher.cancel()
                                    }
                                }
                            }
                    )
                }
                if (userInteractionEnabled && !imageGesturesEnabled) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .pointerInput(view) {
                                detectTapGestures(onTap = { view?.selectImageAt(it.x, it.y) })
                            }
                            .pointerInput(view) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val target = view ?: return@awaitEachGesture
                                    if (target.startHandleDragAt(
                                            down.position.x,
                                            down.position.y
                                        )
                                    ) {
                                        down.consume()
                                        try {
                                            drag(down.id) { change ->
                                                if (currentEvent.changes.count { it.pressed } != 1) {
                                                    target.finishHandleDrag()
                                                } else {
                                                    target.moveHandleDrag(
                                                        change.position.x,
                                                        change.position.y
                                                    )
                                                }
                                                change.consume()
                                            }
                                        } finally {
                                            target.finishHandleDrag()
                                        }
                                        return@awaitEachGesture
                                    }
                                    val held = awaitLongPressOrCancellation(down.id)
                                        ?: return@awaitEachGesture
                                    if (currentEvent.changes.count { it.pressed } != 1) {
                                        return@awaitEachGesture
                                    }
                                    target.startImageDragAt(held.position.x, held.position.y)
                                    try {
                                        val dropped = drag(held.id) { change ->
                                            if (currentEvent.changes.count { it.pressed } != 1) {
                                                target.finishImageDrag(cancelled = true)
                                            } else {
                                                target.moveImageDrag(
                                                    change.position.x,
                                                    change.position.y
                                                )
                                            }
                                            change.consume()
                                        }
                                        target.finishImageDrag(cancelled = !dropped)
                                    } finally {
                                        target.finishImageDrag(cancelled = true)
                                    }
                                }
                            }
                    )
                }
                if (!userInteractionEnabled) {
                    Surface(
                        color = Color.Transparent,
                        modifier = Modifier
                            .matchParentSize()
                            .zIndex(2f)
                    ) { }
                }
                LaunchedEffect(view, collageCreationTrigger) {
                    if (collageCreationTrigger) view?.takeIf { it.hasValidDimensions }?.let {
                        try {
                            imageCreated(it.createImage(outputScaleRatio))
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (throwable: Throwable) {
                            creationFailed(throwable)
                        }
                    }
                }
                LaunchedEffect(view, images, ratio, outputScaleRatio) {
                    view?.outputDimensions(outputScaleRatio)
                        ?.let { (w, h) -> outputSizeChanged?.invoke(w, h) }
                }
            }
        }
    }
}

private class AppliedCollageState {
    val state = CollageStateSync()
    var images: List<Uri>? = null
    var width: Int = 0
    var height: Int = 0
}