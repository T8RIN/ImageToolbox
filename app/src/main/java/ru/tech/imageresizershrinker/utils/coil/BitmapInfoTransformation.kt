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

    private var previousInfo: BitmapInfo = BitmapInfo()

    override val cacheKey: String
        get() = Triple(bitmapInfo, preset, previousInfo).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        var info = bitmapInfo
        if (preset != -1) {
            val currentInfo = info.copy()
            info = preset.applyPresetBy(
                bitmap = input,
                currentInfo = info
            ).let {
                if (previousInfo.quality != currentInfo.quality) {
                    it.copy(quality = currentInfo.quality)
                } else it
            }
        }
        previousInfo = info.copy()
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