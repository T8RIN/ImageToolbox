/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.quick_tiles.screenshot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.projection.MediaProjection
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.getSystemService
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.mainLooperDelayedAction


class ScreenshotMaker(
    private val mediaProjection: MediaProjection,
    private val context: Context,
    private val onSuccess: (Bitmap) -> Unit
) : OnImageAvailableListener {

    private var virtualDisplay: VirtualDisplay? = null

    private var imageReader: ImageReader? = null

    @Suppress("DEPRECATION")
    private val displayMetrics: IntegerSize = run {
        val wm = context.getSystemService<WindowManager>()
            ?: return@run context.resources.displayMetrics.run {
                IntegerSize(
                    width = widthPixels, height = heightPixels
                )
            }

        val bounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wm.currentWindowMetrics.bounds
        } else {
            val display = wm.defaultDisplay
            val size = Point()
            display.getRealSize(size)
            Rect(0, 0, size.x, size.y)
        }

        IntegerSize(bounds.width(), bounds.height())
    }


    fun takeScreenshot(delay: Long) {
        mainLooperDelayedAction(delay) {
            imageReader = ImageReader.newInstance(
                displayMetrics.width,
                displayMetrics.height,
                PixelFormat.RGBA_8888,
                1
            )
            runCatching {
                virtualDisplay = mediaProjection.createVirtualDisplay(
                    "screenshot",
                    displayMetrics.width,
                    displayMetrics.height,
                    context.resources.displayMetrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader?.surface,
                    null,
                    null
                )
                imageReader?.setOnImageAvailableListener(this@ScreenshotMaker, null)
            }
        }
    }

    override fun onImageAvailable(reader: ImageReader) {
        val image = reader.acquireLatestImage() ?: return
        val planes = image.planes
        val buffer = planes[0].buffer.rewind()
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * displayMetrics.width
        val bitmap = createBitmap(
            width = displayMetrics.width + rowPadding / pixelStride,
            height = displayMetrics.height
        )

        bitmap.copyPixelsFromBuffer(buffer)

        finish()

        image.close()

        val resultBitmap = createBitmap(
            width = bitmap.width, height = bitmap.height, config = bitmap.safeConfig
        ).applyCanvas {
            drawColor(Color.Black.toArgb())
            drawBitmap(bitmap)
        }

        onSuccess(resultBitmap)
    }

    private fun finish() {
        virtualDisplay?.release()
        mediaProjection.stop()
        imageReader = null
    }

}