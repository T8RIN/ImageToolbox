package ru.tech.imageresizershrinker.presentation.root.transformation

import android.graphics.Bitmap
import coil.size.Size
import ru.tech.imageresizershrinker.core.android.BitmapUtils.applyPresetBy
import ru.tech.imageresizershrinker.core.android.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.image.Transformation
import coil.transform.Transformation as CoilTransformation

class BitmapInfoTransformation(
    private val imageInfo: ImageInfo,
    private val preset: Int
) : CoilTransformation, Transformation<Bitmap> {

    private var previousInfo: ImageInfo = ImageInfo()

    override val cacheKey: String
        get() = Triple(imageInfo, preset, previousInfo).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        var info = imageInfo
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
            imageInfo = info.copy(
                width = if (info.width * info.height < 512 * 512) info.width * 3 else info.width,
                height = if (info.width * info.height < 512 * 512) info.height * 3 else info.height,
            ),
            onByteCount = {}
        )
    }
}