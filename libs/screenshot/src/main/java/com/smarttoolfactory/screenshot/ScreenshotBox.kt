package com.smarttoolfactory.screenshot

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView

/**
 * A composable that gets screenshot of Composable that is in [content].
 * @param screenshotState state of screenshot that contains [Bitmap].
 * @param content Composable that will be captured to bitmap on action or periodically.
 */
@Composable
fun ScreenshotBox(
    modifier: Modifier = Modifier,
    screenshotState: ScreenshotState,
    content: @Composable () -> Unit,
) {
    val view: View = LocalView.current

    var composableBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    Box(modifier = modifier
        .onGloballyPositioned {
            composableBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.boundsInWindow()
            } else {
                it.boundsInRoot()
            }
        }
    ) {
        content()
    }

    DisposableEffect(Unit) {

        screenshotState.callback = {
            composableBounds?.let { bounds ->
                if (bounds.width == 0f || bounds.height == 0f) return@let

                view.screenshot(bounds) { imageResult: ImageResult ->
                    screenshotState.imageState.value = imageResult

                    if (imageResult is ImageResult.Success) {
                        screenshotState.bitmapState.value = imageResult.data
                    }
                }
            }
        }

        onDispose {
            val bmp = screenshotState.bitmapState.value
            bmp?.apply {
                if (!isRecycled) {
                    recycle()
                }
            }
            screenshotState.bitmapState.value = null
            screenshotState.callback = null
        }
    }
}
