package ru.tech.imageresizershrinker.presentation.root.transformation

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import coil.size.Size
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import coil.transform.Transformation as CoilTransformation

class ImageInfoTransformation(
    private val imageInfo: ImageInfo,
    private val preset: Int,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : CoilTransformation, Transformation<Bitmap> {

    override val cacheKey: String
        get() = (Pair(imageInfo, preset) to imageManager).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        var info = imageInfo
        if (preset != -1) {
            val currentInfo = info.copy()
            info = imageManager.applyPresetBy(
                image = input,
                preset = preset,
                currentInfo = info
            ).let {
                if (it.quality != currentInfo.quality) {
                    it.copy(quality = currentInfo.quality)
                } else it
            }
        }
        return imageManager.createPreview(
            image = input,
            imageInfo = info.copy(
                width = if (info.width * info.height < 512 * 512) info.width * 3 else info.width,
                height = if (info.width * info.height < 512 * 512) info.height * 3 else info.height,
            ),
            onGetByteCount = {}
        )
    }
}