package com.smarttoolfactory.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.ui.geometry.Rect

fun View.screenshot(
    bounds: Rect
): ImageResult {

    try {

        val bitmap = Bitmap.createBitmap(
            bounds.width.toInt(),
            bounds.height.toInt(),
            Bitmap.Config.ARGB_8888,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Above Android O not using PixelCopy throws exception
            // https://stackoverflow.com/questions/58314397/java-lang-illegalstateexception-software-rendering-doesnt-support-hardware-bit
            PixelCopy.request(
                (this.context as Activity).window,
                android.graphics.Rect(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt()
                ),
                bitmap,
                {},
                Handler(Looper.getMainLooper())
            )
        } else {
            val canvas = Canvas(bitmap)
                .apply {
                    translate(-bounds.left, -bounds.top)
                }
            this.draw(canvas)
            canvas.setBitmap(null)
        }
        return ImageResult.Success(bitmap)
    } catch (e: Exception) {
        return ImageResult.Error(e)
    }
}

fun View.screenshot(
    bounds: Rect,
    bitmapCallback: (ImageResult) -> Unit
) {

    try {

        val bitmap = Bitmap.createBitmap(
            bounds.width.toInt(),
            bounds.height.toInt(),
            Bitmap.Config.ARGB_8888,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Above Android O not using PixelCopy throws exception
            // https://stackoverflow.com/questions/58314397/java-lang-illegalstateexception-software-rendering-doesnt-support-hardware-bit
            PixelCopy.request(
                (this.context as Activity).window,
                android.graphics.Rect(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt()
                ),
                bitmap,
                {
                    when (it) {
                        PixelCopy.SUCCESS -> {
                            bitmapCallback.invoke(ImageResult.Success(bitmap))
                        }

                        PixelCopy.ERROR_DESTINATION_INVALID -> {
                            bitmapCallback.invoke(
                                ImageResult.Error(
                                    Exception(
                                        "The destination isn't a valid copy target. " +
                                                "If the destination is a bitmap this can occur " +
                                                "if the bitmap is too large for the hardware to " +
                                                "copy to. " +
                                                "It can also occur if the destination " +
                                                "has been destroyed"
                                    )
                                )
                            )
                        }

                        PixelCopy.ERROR_SOURCE_INVALID -> {
                            bitmapCallback.invoke(
                                ImageResult.Error(
                                    Exception(
                                        "It is not possible to copy from the source. " +
                                                "This can happen if the source is " +
                                                "hardware-protected or destroyed."
                                    )
                                )
                            )
                        }

                        PixelCopy.ERROR_TIMEOUT -> {
                            bitmapCallback.invoke(
                                ImageResult.Error(
                                    Exception(
                                        "A timeout occurred while trying to acquire a buffer " +
                                                "from the source to copy from."
                                    )
                                )
                            )
                        }

                        PixelCopy.ERROR_SOURCE_NO_DATA -> {
                            bitmapCallback.invoke(
                                ImageResult.Error(
                                    Exception(
                                        "The source has nothing to copy from. " +
                                                "When the source is a Surface this means that " +
                                                "no buffers have been queued yet. " +
                                                "Wait for the source to produce " +
                                                "a frame and try again."
                                    )
                                )
                            )
                        }

                        else -> {
                            bitmapCallback.invoke(
                                ImageResult.Error(
                                    Exception(
                                        "The pixel copy request failed with an unknown error."
                                    )
                                )
                            )
                        }
                    }

                },
                Handler(Looper.getMainLooper())
            )
        } else {
            val canvas = Canvas(bitmap)
                .apply {
                    translate(-bounds.left, -bounds.top)
                }
            this.draw(canvas)
            canvas.setBitmap(null)
            bitmapCallback.invoke(ImageResult.Success(bitmap))
        }
    } catch (e: Exception) {
        bitmapCallback.invoke(ImageResult.Error(e))
    }
}

