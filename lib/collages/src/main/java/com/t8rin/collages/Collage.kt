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
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.t8rin.collages.utils.CollageLayoutFactory.createCollageLayouts
import com.t8rin.collages.view.FramePhotoLayout
import kotlin.math.min

@Composable
fun Collage(
    images: List<Uri>,
    modifier: Modifier = Modifier,
    spacing: Float = 0f,
    cornerRadius: Float = 0f,
    backgroundColor: Color = Color.White,
    onCollageCreated: (Bitmap) -> Unit,
    collageCreationTrigger: Boolean,
    collageType: CollageType,
    userInteractionEnabled: Boolean = true,
    aspectRatio: Float = 1f,
    outputScaleRatio: Float = 1.5f,
    onImageTap: ((index: Int) -> Unit)? = null,
    handleDrawable: Drawable? = null,
    disableRotation: Boolean = false,
    enableSnapToBorders: Boolean = false
) {
    var previousSize by rememberSaveable {
        mutableIntStateOf(100)
    }
    var previousAspect by rememberSaveable {
        mutableFloatStateOf(aspectRatio)
    }
    var previousImages by rememberSaveable {
        mutableStateOf(listOf<Uri>())
    }
    var needToInvalidate by remember {
        mutableStateOf(false)
    }
    val ownedCollageLayout by remember(collageType.layout?.title) {
        mutableStateOf(
            collageType.layout?.let { template ->
                createCollageLayouts(template.title)
            }
        )
    }

    LaunchedEffect(collageType.layout?.title) {
        needToInvalidate = true
    }

    AnimatedVisibility(
        visible = ownedCollageLayout != null,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        BoxWithConstraints {
            val size = this.constraints.run { min(maxWidth, maxHeight) }
            var viewInstance by remember {
                mutableStateOf<FramePhotoLayout?>(null)
            }
            var viewState by rememberSaveable {
                mutableStateOf(Bundle.EMPTY)
            }
            DisposableEffect(viewInstance) {
                viewInstance?.restoreInstanceState(viewState)

                onDispose {
                    viewState = Bundle()
                    viewInstance?.saveInstanceState(viewState)
                }
            }
            SideEffect {
                viewInstance?.setBackgroundColor(backgroundColor)
                viewInstance?.setSpace(spacing, cornerRadius)
                viewInstance?.setDisableRotation(disableRotation)
                viewInstance?.setEnableSnapToBorders(enableSnapToBorders)
            }
            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                AndroidView(
                    factory = {
                        FramePhotoLayout(
                            context = it,
                            mPhotoItems = ownedCollageLayout?.photoItemList ?: emptyList()
                        ).apply {
                            updateImages(images)
                            previousImages = images
                            setParamsManager(ownedCollageLayout?.paramsManager)

                            val (width, height) = calculateDimensions(
                                size,
                                constraints,
                                aspectRatio
                            )
                            viewInstance = this
                            previousSize = size
                            previousAspect = aspectRatio
                            setBackgroundColor(backgroundColor)
                            setOnItemTapListener(onImageTap)
                            setHandleDrawable(handleDrawable)
                            setDisableRotation(disableRotation)
                            setEnableSnapToBorders(enableSnapToBorders)
                            build(
                                viewWidth = width,
                                viewHeight = height,
                                space = spacing,
                                corner = cornerRadius
                            )
                        }
                    },
                    update = {
                        if (needToInvalidate) {
                            //Full rebuild
                            needToInvalidate = false

                            it.mPhotoItems = ownedCollageLayout?.photoItemList ?: emptyList()
                            it.updateImages(images)
                            previousImages = images
                            it.setParamsManager(ownedCollageLayout?.paramsManager)

                            it.setOnItemTapListener(onImageTap)
                            it.setHandleDrawable(handleDrawable)
                            previousSize = size
                            previousAspect = aspectRatio

                            val (width, height) = calculateDimensions(
                                size,
                                constraints,
                                aspectRatio
                            )
                            it.build(
                                viewWidth = width,
                                viewHeight = height,
                                space = spacing,
                                corner = cornerRadius
                            )
                        } else {
                            //Readjustments

                            if (previousSize != size || previousAspect != aspectRatio) {
                                val (width, height) = calculateDimensions(
                                    size,
                                    constraints,
                                    aspectRatio
                                )
                                it.resize(width, height)

                                previousSize = size
                                previousAspect = aspectRatio
                            }

                            if (previousImages != images) {
                                it.updateImages(images)
                                previousImages = images
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

            LaunchedEffect(viewInstance, collageCreationTrigger) {
                if (collageCreationTrigger) {
                    viewInstance?.createImage(outputScaleRatio)?.let(onCollageCreated)
                }
            }
        }
    }
}

private fun calculateDimensions(
    size: Int,
    constraints: Constraints,
    aspectRatio: Float
): Pair<Int, Int> {
    return if (size == constraints.maxWidth) {
        val targetHeight =
            (size / aspectRatio).toDouble().coerceAtMost(constraints.maxHeight.toDouble()).toInt()
        val targetWidth = (targetHeight * aspectRatio).toInt()
        targetWidth to targetHeight
    } else {
        val targetWidth =
            (size * aspectRatio).toDouble().coerceAtMost(constraints.maxWidth.toDouble()).toInt()
        val targetHeight = (targetWidth / aspectRatio).toInt()
        targetWidth to targetHeight
    }
}