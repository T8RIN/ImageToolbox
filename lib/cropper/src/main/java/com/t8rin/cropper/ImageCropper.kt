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

package com.t8rin.cropper

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.t8rin.cropper.crop.CropAgent
import com.t8rin.cropper.draw.DrawingOverlay
import com.t8rin.cropper.draw.ImageDrawCanvas
import com.t8rin.cropper.image.ImageWithConstraints
import com.t8rin.cropper.image.getScaledImageBitmap
import com.t8rin.cropper.model.CropOutline
import com.t8rin.cropper.settings.CropDefaults
import com.t8rin.cropper.settings.CropProperties
import com.t8rin.cropper.settings.CropStyle
import com.t8rin.cropper.settings.CropType
import com.t8rin.cropper.state.DynamicCropState
import com.t8rin.cropper.state.rememberCropState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    sourceImageUri: Uri? = null,
    sourceImageSize: IntSize = IntSize(imageBitmap.width, imageBitmap.height),
    contentDescription: String? = null,
    cropStyle: CropStyle = CropDefaults.style(),
    cropProperties: CropProperties,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    crop: Boolean = false,
    enableOneFingerZoom: Boolean = true,
    isOverlayDraggable: Boolean = true,
    onCropStart: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onCropSuccess: (Uri?) -> Unit,
    backgroundModifier: Modifier = Modifier
) {
    val minCropDimension = with(LocalDensity.current) {
        MinCropDimension.roundToPx()
    }
    val resolvedCropProperties = remember(cropProperties, minCropDimension) {
        if (cropProperties.minDimension == null) {
            cropProperties.copy(
                minDimension = IntSize(minCropDimension, minCropDimension)
            )
        } else {
            cropProperties
        }
    }

    ImageWithConstraints(
        modifier = modifier.clipToBounds(),
        contentScale = resolvedCropProperties.contentScale,
        contentDescription = contentDescription,
        filterQuality = filterQuality,
        imageBitmap = imageBitmap,
        drawImage = false
    ) {

        // No crop operation is applied by ScalableImage so rect points to bounds of original
        // bitmap
        val scaledImageBitmap = getScaledImageBitmap(
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            rect = rect,
            bitmap = imageBitmap,
            contentScale = resolvedCropProperties.contentScale,
        )

        // Container Dimensions
        val containerWidthPx = constraints.maxWidth
        val containerHeightPx = constraints.maxHeight

        val containerWidth: Dp
        val containerHeight: Dp

        // Bitmap Dimensions
        val bitmapWidth = scaledImageBitmap.width
        val bitmapHeight = scaledImageBitmap.height

        // Dimensions of Composable that displays Bitmap
        val imageWidthPx: Int
        val imageHeightPx: Int

        with(LocalDensity.current) {
            imageWidthPx = imageWidth.roundToPx()
            imageHeightPx =
                imageHeight.roundToPx() - if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) 0
                else WindowInsets.navigationBars.getBottom(LocalDensity.current)
            containerWidth = containerWidthPx.toDp()
            containerHeight = containerHeightPx.toDp()
        }

        val cropType = resolvedCropProperties.cropType
        val contentScale = resolvedCropProperties.contentScale
        val fixedAspectRatio = resolvedCropProperties.fixedAspectRatio
        val cropOutline = resolvedCropProperties.cropOutlineProperty.cropOutline

        // these keys are for resetting cropper when image width/height, contentScale or
        // overlay aspect ratio changes
        val resetKeys =
            getResetKeys(
                scaledImageBitmap,
                imageWidthPx,
                imageHeightPx,
                contentScale,
                cropType,
                fixedAspectRatio
            )

        val cropState = rememberCropState(
            imageSize = IntSize(bitmapWidth, bitmapHeight),
            containerSize = IntSize(containerWidthPx, containerHeightPx),
            drawAreaSize = IntSize(imageWidthPx, imageHeightPx),
            cropProperties = resolvedCropProperties,
            isOverlayDraggable = isOverlayDraggable,
            keys = resetKeys
        )

        LaunchedEffect(cropState, isOverlayDraggable) {
            if (cropState is DynamicCropState) {
                cropState.isOverlayDraggable = isOverlayDraggable
            }
        }

        onZoomChange(cropState.zoom)

        val selectedHandle = if (cropState is DynamicCropState) {
            cropState.pressedHandle
        } else {
            TouchRegion.None
        }
        var animatedHandle by remember(cropState) {
            mutableStateOf(TouchRegion.None)
        }
        val selectedHandleScale = remember(cropState) {
            Animatable(1f)
        }
        LaunchedEffect(selectedHandle) {
            if (handlesTouched(selectedHandle)) {
                animatedHandle = selectedHandle
                selectedHandleScale.animateTo(1.4f)
            } else {
                selectedHandleScale.animateTo(1f)
                animatedHandle = TouchRegion.None
            }
        }

        // Crops image when user invokes crop operation
        Crop(
            crop = crop,
            scaledImageBitmap = scaledImageBitmap,
            sourceImageUri = sourceImageUri,
            sourceImageSize = sourceImageSize,
            sourceImageVisibleRect = rect,
            previewImageSize = IntSize(imageBitmap.width, imageBitmap.height),
            cropRect = cropState.cropRect,
            cropOutline = cropOutline,
            onCropStart = onCropStart,
            onCropSuccess = onCropSuccess
        )

        val imageModifier = Modifier
            .size(containerWidth, containerHeight)
            .crop(
                keys = resetKeys,
                cropState = cropState,
                enableOneFingerZoom = enableOneFingerZoom
            )

        LaunchedEffect(key1 = resolvedCropProperties) {
            cropState.updateProperties(resolvedCropProperties)
        }

        /// Create a MutableTransitionState<Boolean> for the AnimatedVisibility.
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(100)
            visible = true
        }

        ImageCropper(
            modifier = imageModifier,
            visible = visible,
            imageBitmap = imageBitmap,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            imageWidthPx = imageWidthPx,
            imageHeightPx = imageHeightPx,
            middleHandleSize = resolvedCropProperties.middleHandleSize,
            selectedHandle = animatedHandle,
            selectedHandleScale = selectedHandleScale.value,
            overlayRect = cropState.overlayRect,
            cropType = cropType,
            cropOutline = cropOutline,
            cropStyle = cropStyle,
            transparentColor = cropStyle.backgroundColor,
            backgroundModifier = backgroundModifier
        )
    }
}

@Composable
private fun ImageCropper(
    modifier: Modifier,
    visible: Boolean,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    middleHandleSize: Float,
    selectedHandle: TouchRegion,
    selectedHandleScale: Float,
    cropType: CropType,
    cropOutline: CropOutline,
    cropStyle: CropStyle,
    overlayRect: Rect,
    transparentColor: Color,
    backgroundModifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(backgroundModifier)
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500))
        ) {
            ImageCropperImpl(
                modifier = modifier,
                imageBitmap = imageBitmap,
                containerWidth = containerWidth,
                containerHeight = containerHeight,
                imageWidthPx = imageWidthPx,
                imageHeightPx = imageHeightPx,
                cropType = cropType,
                cropOutline = cropOutline,
                middleHandleSize = middleHandleSize,
                selectedHandle = selectedHandle,
                selectedHandleScale = selectedHandleScale,
                cropStyle = cropStyle,
                rectOverlay = overlayRect,
                transparentColor = transparentColor
            )
        }
    }
}

@Composable
private fun ImageCropperImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    cropType: CropType,
    cropOutline: CropOutline,
    middleHandleSize: Float,
    selectedHandle: TouchRegion,
    selectedHandleScale: Float,
    cropStyle: CropStyle,
    transparentColor: Color,
    rectOverlay: Rect
) {

    Box(contentAlignment = Alignment.Center) {

        // Draw Image
        ImageDrawCanvas(
            modifier = modifier,
            imageBitmap = imageBitmap,
            imageWidth = imageWidthPx,
            imageHeight = imageHeightPx
        )

        val drawOverlay = cropStyle.drawOverlay

        val drawGrid = cropStyle.drawGrid
        val overlayColor = cropStyle.overlayColor
        val handleColor = cropStyle.handleColor
        val drawHandles = cropType == CropType.Dynamic
        val strokeWidth = cropStyle.strokeWidth

        DrawingOverlay(
            modifier = Modifier.size(containerWidth, containerHeight),
            drawOverlay = drawOverlay,
            rect = rectOverlay,
            cropOutline = cropOutline,
            drawGrid = drawGrid,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            drawHandles = drawHandles,
            middleHandleSize = middleHandleSize,
            selectedHandle = selectedHandle,
            selectedHandleScale = selectedHandleScale,
            transparentColor = transparentColor,
        )

    }
}

@Composable
private fun Crop(
    crop: Boolean,
    scaledImageBitmap: ImageBitmap,
    sourceImageUri: Uri?,
    sourceImageSize: IntSize,
    sourceImageVisibleRect: IntRect,
    previewImageSize: IntSize,
    cropRect: Rect,
    cropOutline: CropOutline,
    onCropStart: () -> Unit,
    onCropSuccess: (Uri?) -> Unit
) {

    val context = LocalContext.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // Crop Agent is responsible for cropping image
    val cropAgent = remember { CropAgent() }

    LaunchedEffect(crop) {
        if (crop) {
            flow {
                val previewSize = IntSize(scaledImageBitmap.width, scaledImageBitmap.height)
                emit(
                    cropAgent.cropToCache(
                        context = context,
                        imageUri = sourceImageUri,
                        fallbackImageBitmap = scaledImageBitmap,
                        fallbackCropRect = cropRect,
                        sourceCropRect = cropRect.scaleTo(
                            sourceSize = sourceImageSize,
                            previewSize = previewSize,
                            sourceImageVisibleRect = sourceImageVisibleRect,
                            previewImageSize = previewImageSize
                        ),
                        cropOutline = cropOutline,
                        layoutDirection = layoutDirection,
                        density = density
                    )
                )
            }
                .flowOn(Dispatchers.Default)
                .onStart {
                    onCropStart()
                    delay(400)
                }
                .onEach {
                    onCropSuccess(it)
                }
                .launchIn(this)
        }
    }
}

private fun Rect.scaleTo(
    sourceSize: IntSize,
    previewSize: IntSize,
    sourceImageVisibleRect: IntRect,
    previewImageSize: IntSize
): Rect {
    if (sourceSize == IntSize.Zero || previewSize == IntSize.Zero) return this

    val widthScale = sourceSize.width.toFloat() / previewImageSize.width.coerceAtLeast(1)
    val heightScale = sourceSize.height.toFloat() / previewImageSize.height.coerceAtLeast(1)

    return Rect(
        left = (sourceImageVisibleRect.left + left) * widthScale,
        top = (sourceImageVisibleRect.top + top) * heightScale,
        right = (sourceImageVisibleRect.left + right) * widthScale,
        bottom = (sourceImageVisibleRect.top + bottom) * heightScale
    )
}

@Composable
private fun getResetKeys(
    scaledImageBitmap: ImageBitmap,
    imageWidthPx: Int,
    imageHeightPx: Int,
    contentScale: ContentScale,
    cropType: CropType,
    fixedAspectRatio: Boolean,
) = remember(
    scaledImageBitmap,
    imageWidthPx,
    imageHeightPx,
    contentScale,
    cropType,
    fixedAspectRatio,
) {
    arrayOf(
        scaledImageBitmap,
        imageWidthPx,
        imageHeightPx,
        contentScale,
        cropType,
        fixedAspectRatio,
    )
}

private val MinCropDimension = 100.dp
