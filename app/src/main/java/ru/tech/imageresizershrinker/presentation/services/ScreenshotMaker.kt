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