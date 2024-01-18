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

package ru.tech.imageresizershrinker.presentation.services

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.projection.MediaProjection
import android.util.DisplayMetrics


class ScreenshotMaker(
    private var mMediaProjection: MediaProjection?,
    private val displayMetrics: DisplayMetrics
) : OnImageAvailableListener {

    private var virtualDisplay: VirtualDisplay? = null

    private var callback: (Bitmap) -> Unit = {}
    private var mImageReader: ImageReader? = null


    fun takeScreenshot(callback: (Bitmap) -> Unit) {
        this.callback = callback
        mImageReader = ImageReader.newInstance(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            PixelFormat.RGBA_8888,
            1
        )
        runCatching {
            virtualDisplay = mMediaProjection!!.createVirtualDisplay(
                "screenshot",
                displayMetrics.widthPixels,
                displayMetrics.heightPixels,
                displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader!!.surface,
                null,
                null
            )
            mImageReader!!.setOnImageAvailableListener(this@ScreenshotMaker, null)
        }
    }

    override fun onImageAvailable(reader: ImageReader) {
        val image = reader.acquireLatestImage() ?: return
        val planes = image.planes
        val buffer = planes[0].buffer.rewind()
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * displayMetrics.widthPixels
        val bitmap = Bitmap.createBitmap(
            displayMetrics.widthPixels + rowPadding / pixelStride,
            displayMetrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        tearDown()
        image.close()
        callback(bitmap)
    }

    private fun tearDown() {
        virtualDisplay!!.release()
        if (mMediaProjection != null) mMediaProjection!!.stop()
        mMediaProjection = null
        mImageReader = null
    }

}