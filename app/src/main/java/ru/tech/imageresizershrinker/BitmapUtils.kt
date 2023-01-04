package ru.tech.imageresizershrinker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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

    fun Bitmap.resizeBitmap(maxLength: Int): Bitmap {
        return try {
            if (height >= width) {
                val aspectRatio = width.toDouble() / height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(this, targetWidth, maxLength, false)
            } else {
                val aspectRatio = height.toDouble() / width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(this, maxLength, targetHeight, false)
            }
        } catch (_: Exception) {
            this
        }
    }


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

        if (explicit) {
            Bitmap.createScaledBitmap(
                this,
                tWidth,
                tHeight,
                false
            )
        } else {
            this.resizeBitmap(max(tWidth, tHeight))
        }
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