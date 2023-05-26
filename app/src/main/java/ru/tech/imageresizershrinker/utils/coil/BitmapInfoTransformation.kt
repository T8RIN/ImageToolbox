package ru.tech.imageresizershrinker.utils.coil

import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.applyPresetBy
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.previewBitmap

class BitmapInfoTransformation(
    private val bitmapInfo: BitmapInfo,
    private val preset: Int
) : Transformation {
    override val cacheKey: String
        get() = Pair(bitmapInfo, preset).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        var info = bitmapInfo
        if (preset != -1) {
            info = preset.applyPresetBy(
                bitmap = input,
                currentInfo = info
            )
        }
        return input.previewBitmap(
            quality = info.quality,
            widthValue = info.width,
            heightValue = info.height,
            mimeTypeInt = info.mimeTypeInt,
            resizeType = info.resizeType,
            rotationDegrees = info.rotationDegrees,
            isFlipped = info.isFlipped,
            onByteCount = {}
        )
    }
}