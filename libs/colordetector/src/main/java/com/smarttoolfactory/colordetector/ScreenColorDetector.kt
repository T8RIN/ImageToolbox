package com.smarttoolfactory.colordetector

import android.graphics.Bitmap
import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.ScreenRefreshPolicy.OnDown
import com.smarttoolfactory.colordetector.ScreenRefreshPolicy.OnEnable
import com.smarttoolfactory.colordetector.ScreenRefreshPolicy.OnUp
import com.smarttoolfactory.colordetector.model.ColorData
import com.smarttoolfactory.colordetector.util.calculateColorInPixel
import com.smarttoolfactory.gesture.pointerMotionEvents
import com.smarttoolfactory.image.ImageWithThumbnail
import com.smarttoolfactory.image.rememberThumbnailState
import com.smarttoolfactory.screenshot.ImageResult
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * A Composable that detect color at pixel that user touches when [enabled].
 * @param enabled when enabled detect color at user's point of touch
 * @param thumbnailSize size of the thumbnail that displays touch position with zoom
 * @param thumbnailZoom zoom scale between 100% and 500%
 * @param screenRefreshPolicy how to set or refresh screenshot of the screen. By default
 * screenshot is taken when [enabled] flag is set to true after delay specified
 * with [delayBeforeCapture]
 * If [OnDown] or [OnUp] is selected screenshot is taken when [enabled] is false and when
 * first pointer is down or last pointer is
 * up after delay specified with [delayBeforeCapture]
 * @param delayBeforeCapture how many milliseconds should be waited before taking screenshot
 * of the screen
 * @param content is screen/Composable is displayed to user to get color from.
 * @param onColorChange callback to notify that user moved and picked a color
 */
@Composable
fun ScreenColorDetector(
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    thumbnailSize: Dp = 80.dp,
    @IntRange(from = 100, to = 500) thumbnailZoom: Int = 200,
    screenRefreshPolicy: ScreenRefreshPolicy = OnEnable,
    content: @Composable () -> Unit,
    delayBeforeCapture: Long = 0L,
    onColorChange: (ColorData) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var offset by remember {
        mutableStateOf(Offset.Unspecified)
    }

    var center by remember {
        mutableStateOf(Offset.Unspecified)
    }

    var labelSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val screenshotState = rememberScreenshotState()

    LaunchedEffect(key1 = enabled) {
        if (enabled && screenRefreshPolicy == OnEnable) {
            delay(delayBeforeCapture)
            screenshotState.capture()
        } else {
            screenshotState.imageState.value = ImageResult.Initial
            offset = Offset.Unspecified
            center = Offset.Unspecified
        }
    }

    var color by remember { mutableStateOf(Color.Unspecified) }
    val colorName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        snapshotFlow { color }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
            .collect {
                onColorChange(ColorData(color, color.toString()))
            }
    }

    Box {

        ScreenshotBox(
            modifier = modifier.pointerMotionEvents(
                onDown = {
                    if (screenRefreshPolicy == OnDown && !enabled) {
                        coroutineScope.launch {
                            delay(delayBeforeCapture)
                            screenshotState.capture()
                        }
                    }
                },
                onUp = {
                    if (screenRefreshPolicy == OnUp && !enabled) {
                        coroutineScope.launch {
                            delay(delayBeforeCapture)
                            screenshotState.capture()
                        }
                    }
                }
            ),
            screenshotState = screenshotState
        ) {
            content()
        }

        if (enabled) {

            val imageResult = screenshotState.imageState.value
            if (imageResult is ImageResult.Success) {

                val bitmap = imageResult.data
                val density = LocalDensity.current.density
                val imageWidth = (bitmap.width / density).dp
                val imageHeight = (bitmap.height / density).dp

                ScreenColorDetectorImpl(
                    modifier = Modifier.size(imageWidth, imageHeight),
                    enabled = enabled,
                    bitmap = bitmap,
                    thumbnailSize = thumbnailSize,
                    thumbnailZoom = thumbnailZoom,
                    colorData = ColorData(color, colorName),
                    center = center,
                    offset = offset,
                    onColorChange = {
                        color = it
                    },
                    onTouchEvent = {
                        offset = it
                    },
                    onThumbnailCenterChange = {
                        center = it
                    }
                )

                if (offset.isSpecified && offset.isFinite) {

                    val labelOffsetX = if (offset.x < bitmap.width - labelSize.width) {
                        offset.x + 16.dp.value * density
                    } else {
                        offset.x - labelSize.width - 16.dp.value * density
                    }.toInt()

                    val labelOffsetY = (offset.y - labelSize.height / 2).toInt()

                    ColorDisplay(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    labelOffsetX,
                                    labelOffsetY
                                )
                            }
                            .onSizeChanged {
                                labelSize = it
                            },
                        ColorData(color, colorName)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenColorDetectorImpl(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    bitmap: Bitmap,
    thumbnailSize: Dp,
    thumbnailZoom: Int,
    colorData: ColorData,
    offset: Offset,
    center: Offset,
    onColorChange: (Color) -> Unit,
    onTouchEvent: (Offset) -> Unit,
    onThumbnailCenterChange: (Offset) -> Unit
) {
    val density = LocalDensity.current.density
    val imageBitmap by remember {
        mutableStateOf(bitmap.asImageBitmap())
    }
    ImageWithThumbnail(
        imageBitmap = imageBitmap,
        modifier = modifier,
        contentDescription = "Screen Color Detector",
        contentScale = ContentScale.FillBounds,
        thumbnailState = rememberThumbnailState(
            size = DpSize(thumbnailSize, thumbnailSize),
            thumbnailZoom = thumbnailZoom
        ),
        onThumbnailCenterChange = {
            onThumbnailCenterChange(it)
        },
        onDown = {
            onTouchEvent(it)
        },
        onMove = {
            onTouchEvent(it)
        }
    ) {

        if (enabled && offset.isSpecified && offset.isFinite) {

            onColorChange(
                calculateColorInPixel(
                    offsetX = offset.x,
                    offsetY = offset.y,
                    startImageX = 0f,
                    startImageY = 0f,
                    rect = rect,
                    width = imageWidth.value * density,
                    height = imageHeight.value * density,
                    bitmap = bitmap
                )
            )

            ColorSelectionDrawing(
                modifier = Modifier.size(imageWidth, imageHeight),
                offset = offset
            )
        }
    }
}

/**
 * Enum class fo screen refresh policy
 * * When [OnEnable] is selected screenshot is taken on each on [ScreenColorDetector] is enabled
 * after specified delay
 * * When [OnDown] is selected screenshot is taken if
 * [ScreenColorDetector] is disabled after specified delay
 * * When [OnUp] is selected screenshot is taken after last pointer on screen is up if
 * [ScreenColorDetector] is disabled after specified delay
 */
enum class ScreenRefreshPolicy {
    OnEnable, OnDown, OnUp
}