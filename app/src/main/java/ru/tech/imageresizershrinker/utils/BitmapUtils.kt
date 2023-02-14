package ru.tech.imageresizershrinker.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Size
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import kotlin.math.max


object BitmapUtils {

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun Bitmap.flip(value: Boolean): Bitmap {
        return if (value) {
            val matrix = Matrix().apply { postScale(-1f, 1f, width / 2f, width / 2f) }
            Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        } else this
    }

    fun Bitmap.resizeBitmap(width_: Int, height_: Int, adaptiveResize: Boolean): Bitmap {
        val max = max(width_, height_)
        return if (!adaptiveResize) {
            Bitmap.createScaledBitmap(
                this,
                width_,
                height_,
                false
            )
        } else {
            kotlin.runCatching {
                if (height >= width) {
                    val aspectRatio = width.toDouble() / height.toDouble()
                    val targetWidth = (max * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(this, targetWidth, max, false)
                } else {
                    val aspectRatio = height.toDouble() / width.toDouble()
                    val targetHeight = (max * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(this, max, targetHeight, false)
                }
            }.getOrNull() ?: this
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, requiredSize: Size): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height * width > requiredSize.height * requiredSize.width) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            do {
                inSampleSize *= 2
            } while ((halfHeight * halfWidth) / (inSampleSize * inSampleSize) >= requiredSize.height * requiredSize.width)

        }

        return inSampleSize
    }

    fun Context.decodeSampledBitmap(
        uri: Uri,
        requiredSize: Size,
        outPadding: Rect? = null,
        options: BitmapFactory.Options = BitmapFactory.Options()
    ): Bitmap? {
        return options.run {
            // First decode with inJustDecodeBounds=true to check dimensions
            inJustDecodeBounds = true
            decodeBitmapFromUri(uri, outPadding, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, requiredSize)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false
            decodeBitmapFromUri(uri, outPadding, this)
        }
    }

    fun Context.decodeBitmapFromUri(
        uri: Uri,
        outPadding: Rect? = null,
        options: BitmapFactory.Options = BitmapFactory.Options()
    ): Bitmap? = kotlin.runCatching {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        BitmapFactory.decodeFileDescriptor(fileDescriptor, outPadding, options).also {
            parcelFileDescriptor?.close()
        }
    }.getOrNull()

    fun Bitmap.previewBitmap(
        quality: Float,
        widthValue: Int?,
        heightValue: Int?,
        mime: Int,
        resize: Int,
        rotation: Float,
        isFlipped: Boolean,
        onByteCount: (Int) -> Unit
    ): Bitmap {
        val out = ByteArrayOutputStream()
        val explicit = resize == 0
        val tWidth = widthValue ?: width
        val tHeight = heightValue ?: height

        resizeBitmap(tWidth, tHeight, !explicit)
            .rotate(rotation)
            .flip(isFlipped)
            .compress(
                if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 0) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                quality.toInt(), out
            )
        val b = out.toByteArray()
        onByteCount(b.size)
        val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(b))

        out.flush()
        out.close()

        return decoded

    }
}