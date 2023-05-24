package ru.tech.imageresizershrinker.utils.coil

import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.previewBitmap

class BitmapInfoTransformation(
    private val bitmapInfo: BitmapInfo
) : Transformation {
    override val cacheKey: String
        get() = bitmapInfo.hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return input.previewBitmap(
            quality = bitmapInfo.quality,
            widthValue = bitmapInfo.width,
            heightValue = bitmapInfo.height,
            mimeTypeInt = bitmapInfo.mimeTypeInt,
            resizeType = bitmapInfo.resizeType,
            rotationDegrees = bitmapInfo.rotationDegrees,
            isFlipped = bitmapInfo.isFlipped,
            onByteCount = {}
        )
    }
}