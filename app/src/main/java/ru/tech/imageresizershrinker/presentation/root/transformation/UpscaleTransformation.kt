package ru.tech.imageresizershrinker.presentation.root.transformation

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.domain.image.Transformation
import java.lang.Integer.max
import coil.transform.Transformation as CoilTransformation

class UpscaleTransformation(
    private val fraction: Float = 2f
) : CoilTransformation, Transformation<Bitmap> {
    override val cacheKey: String
        get() = fraction.hashCode().toString() + this::class.simpleName

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val (w, h) = input.width to input.height
        if (w * h < 2000 * 2000) {
            return kotlin.runCatching {
                val he = max(h * fraction.toInt(), 2000)
                val wi = max(w * fraction.toInt(), 2000)
                val max = max(wi, he)
                if (input.height >= input.width) {
                    val aspectRatio = input.width.toDouble() / input.height.toDouble()
                    val targetWidth = (max * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(input, targetWidth, max, false)
                } else {
                    val aspectRatio = input.height.toDouble() / input.width.toDouble()
                    val targetHeight = (max * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(input, max, targetHeight, false)
                }
            }.getOrNull() ?: input
        }
        return input
    }
}