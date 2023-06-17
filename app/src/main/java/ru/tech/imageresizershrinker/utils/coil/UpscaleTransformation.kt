package ru.tech.imageresizershrinker.utils.coil

import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import java.lang.Integer.max

class UpscaleTransformation(
    private val fraction: Float = 2f
) : Transformation {
    override val cacheKey: String
        get() = fraction.hashCode().toString() + this::class.simpleName

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val (w, h) = input.width to input.height
        if (w * h < 1500 * 1500) {
            return input.resizeBitmap(
                max(w * fraction.toInt(), 1500),
                max(h * fraction.toInt(), 1500),
                1
            )
        }
        return input
    }
}