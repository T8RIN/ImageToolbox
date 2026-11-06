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

package com.t8rin.collages

import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.t8rin.collages.utils.CollageLayoutFactory.createCollageLayouts
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
    aspectRatio: Float = 1f,
    outputScaleRatio: Float = 1.5f,
    onImageTap: ((index: Int) -> Unit)? = null,
    handleDrawable: Drawable? = null,
    disableRotation: Boolean = false,
    enableSnapToBorders: Boolean = false,
    backgroundShader: ((Float, Float) -> Shader)? = null,
    state: CollageState? = null,
    onStateReady: ((CollageState) -> Unit)? = null,
    onOutputSizeChanged: ((Int, Int) -> Unit)? = null,
    onUserStateChange: ((before: CollageState, after: CollageState) -> Unit)? = null
) {
    val layout = remember(collageType.layoutId) {
        collageType.layoutId?.let(::createCollageLayouts)
    }
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
                        factory = { context ->
                            FramePhotoLayout(context, layout?.photoItemList.orEmpty()).also {
                                it.setParamsManager(layout?.paramsManager)
                                view = it
                            }
                        },
                        onRelease = { it.release() },
                        update = { target ->
                            target.setStateListeners(
                                layoutId = layout?.title.orEmpty(),
                                onStateReady = {
                                    applied.state = it
                                    stateReady?.invoke(it)
                                },
                                onUserStateChange = { before, after ->
                                    stateChanged?.invoke(
                                        before,
                                        after
                                    )
                                }
                            )
                            target.setBackgroundColor(backgroundColor)
                            target.setBackgroundShader(backgroundShader)
                            target.setOnItemTapListener(onImageTap)
                            target.setHandleDrawable(handleDrawable)
                            target.setDisableRotation(disableRotation)
                            target.setEnableSnapToBorders(enableSnapToBorders)
                            target.outputAspectRatio = ratio
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
                            state?.takeIf { it.layoutId == layout?.title && it != applied.state }
                                ?.let {
                                    applied.state = it
                                    target.restoreState(it)
                                }
                        }
                    )
                }
                if (userInteractionEnabled && !imageGesturesEnabled) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .pointerInput(view) {
                                detectTapGestures(
                                    onTap = { view?.selectImageAt(it.x, it.y) },
                                    onLongPress = {
                                        view?.selectImageAt(
                                            it.x,
                                            it.y,
                                            longPress = true
                                        )
                                    }
                                )
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
    var state: CollageState? = null
    var images: List<Uri>? = null
    var width: Int = 0
    var height: Int = 0
}