package com.smarttoolfactory.cropper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.crop.CropAgent
import com.smarttoolfactory.cropper.draw.DrawingOverlay
import com.smarttoolfactory.cropper.draw.ImageDrawCanvas
import com.smarttoolfactory.cropper.image.ImageWithConstraints
import com.smarttoolfactory.cropper.image.getScaledImageBitmap
import com.smarttoolfactory.cropper.model.CropOutline
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropProperties
import com.smarttoolfactory.cropper.settings.CropStyle
import com.smarttoolfactory.cropper.settings.CropType
import com.smarttoolfactory.cropper.state.DynamicCropState
import com.smarttoolfactory.cropper.state.rememberCropState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentDescription: String? = null,
    cropStyle: CropStyle = CropDefaults.style(),
    cropProperties: CropProperties,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    crop: Boolean = false,
    onCropStart: () -> Unit,
    onCropSuccess: (ImageBitmap) -> Unit
) {

    ImageWithConstraints(
        modifier = modifier.clipToBounds(),
        contentScale = cropProperties.contentScale,
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
            contentScale = cropProperties.contentScale,
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
            imageHeightPx = imageHeight.roundToPx()
            containerWidth = containerWidthPx.toDp()
            containerHeight = containerHeightPx.toDp()
        }

        val cropType = cropProperties.cropType
        val contentScale = cropProperties.contentScale

        val cropOutline = cropProperties.cropOutlineProperty.cropOutline

        // these keys are for resetting cropper when image width/height, contentScale or
        // overlay aspect ratio changes
        val resetKeys =
            getResetKeys(scaledImageBitmap, imageWidthPx, imageHeightPx, contentScale, cropType)

        val cropState = rememberCropState(
            imageSize = IntSize(bitmapWidth, bitmapHeight),
            containerSize = IntSize(containerWidthPx, containerHeightPx),
            drawAreaSize = IntSize(imageWidthPx, imageHeightPx),
            cropProperties = cropProperties,
            keys = resetKeys
        )

        val isHandleTouched by remember(cropState) {
            derivedStateOf {
                cropState is DynamicCropState && handlesTouched(cropState.touchRegion)
            }
        }

        val pressedStateColor = remember(cropStyle.backgroundColor){
            cropStyle.backgroundColor
                .copy(cropStyle.backgroundColor.alpha * .7f)
        }

        val transparentColor by animateColorAsState(
            animationSpec = tween(300, easing = LinearEasing),
            targetValue = if (isHandleTouched) pressedStateColor else cropStyle.backgroundColor
        )

        // Crops image when user invokes crop operation
        Crop(
            crop,
            scaledImageBitmap,
            cropState.cropRect,
            cropOutline,
            onCropStart,
            onCropSuccess
        )

        val imageModifier = Modifier
            .size(containerWidth, containerHeight)
            .crop(
                keys = resetKeys,
                cropState = cropState
            )

        LaunchedEffect(key1 = cropProperties) {
            cropState.updateProperties(cropProperties)
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
            handleSize = cropProperties.handleSize,
            overlayRect = cropState.overlayRect,
            cropType = cropType,
            cropOutline = cropOutline,
            cropStyle = cropStyle,
            transparentColor = transparentColor
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ImageCropper(
    modifier: Modifier,
    visible: Boolean,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    handleSize: Float,
    cropType: CropType,
    cropOutline: CropOutline,
    cropStyle: CropStyle,
    overlayRect: Rect,
    transparentColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(tween(500))
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
                handleSize = handleSize,
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
    handleSize: Float,
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
            handleSize = handleSize,
            transparentColor = transparentColor,
        )

    }
}

@Composable
private fun Crop(
    crop: Boolean,
    scaledImageBitmap: ImageBitmap,
    cropRect: Rect,
    cropOutline: CropOutline,
    onCropStart: () -> Unit,
    onCropSuccess: (ImageBitmap) -> Unit
) {

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    // Crop Agent is responsible for cropping image
    val cropAgent = remember { CropAgent() }

    LaunchedEffect(crop) {
        if (crop) {
            flow {
                emit(
                    cropAgent.crop(
                        scaledImageBitmap,
                        cropRect,
                        cropOutline,
                        layoutDirection,
                        density
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

@Composable
private fun getResetKeys(
    scaledImageBitmap: ImageBitmap,
    imageWidthPx: Int,
    imageHeightPx: Int,
    contentScale: ContentScale,
    cropType: CropType
) = remember(
    scaledImageBitmap,
    imageWidthPx,
    imageHeightPx,
    contentScale,
    cropType
) {
    arrayOf(
        scaledImageBitmap,
        imageWidthPx,
        imageHeightPx,
        contentScale,
        cropType
    )
}