package ru.tech.imageresizershrinker.presentation.root.model.transformation

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.core.android.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.domain.model.ResizeType
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
            return input.resizeBitmap(
                width_ = max(w * fraction.toInt(), 2000),
                height_ = max(h * fraction.toInt(), 2000),
                resizeType = ResizeType.Flexible
            )
        }
        return input
    }
}