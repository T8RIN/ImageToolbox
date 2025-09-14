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
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.mainLooperDelayedAction
import com.t8rin.logger.makeLog


class ScreenshotMaker(
    private val mediaProjection: MediaProjection,
    private val context: Context,
    private val onSuccess: (Bitmap) -> Unit
) : OnImageAvailableListener {

    private var virtualDisplay: VirtualDisplay? = null

    private var imageReader: ImageReader? = null

    @Suppress("DEPRECATION")
    private val screenSize: IntegerSize = run {
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
    }.also {
        it.makeLog("Acquired screen size for screenshot")
    }

    fun takeScreenshot(delay: Long) {
        mainLooperDelayedAction(delay) {
            imageReader = ImageReader.newInstance(
                screenSize.width,
                screenSize.height,
                PixelFormat.RGBA_8888,
                1
            )
            runCatching {
                virtualDisplay = mediaProjection.createVirtualDisplay(
                    "screenshot",
                    screenSize.width,
                    screenSize.height,
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
        val image = reader.acquireLatestImage() ?: return takeScreenshot(300)
        val planes = image.planes
        val buffer = planes[0].buffer.rewind()
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * screenSize.width

        val bitmap = createBitmap(
            width = screenSize.width + rowPadding / pixelStride,
            height = screenSize.height
        )

        bitmap.copyPixelsFromBuffer(buffer)

        finish()

        image.close()

        onSuccess(bitmap)
    }

    private fun finish() {
        virtualDisplay?.release()
        mediaProjection.stop()
        imageReader = null
    }

}