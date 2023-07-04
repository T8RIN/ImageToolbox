package ru.tech.imageresizershrinker.presentation.root.model.transformation

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.core.android.BitmapUtils.applyPresetBy
import ru.tech.imageresizershrinker.core.android.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.domain.model.BitmapInfo
import ru.tech.imageresizershrinker.domain.image.Transformation
import coil.transform.Transformation as CoilTransformation

class BitmapInfoTransformation(
    private val bitmapInfo: BitmapInfo,
    private val preset: Int
) : CoilTransformation, Transformation<Bitmap> {

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
            bitmapInfo = info.copy(
                width = if (info.width * info.height < 512 * 512) info.width * 3 else info.width,
                height = if (info.width * info.height < 512 * 512) info.height * 3 else info.height,
            ),
            onByteCount = {}
        )
    }
}