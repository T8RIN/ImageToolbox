package ru.tech.imageresizershrinker.presentation.root.transformation

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import coil.size.Size
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.Preset
import ru.tech.imageresizershrinker.domain.model.ResizeType
import coil.transform.Transformation as CoilTransformation

class ImageInfoTransformation(
    private val imageInfo: ImageInfo,
    private val preset: Preset = Preset.Numeric(100),
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val transformations: List<Transformation<Bitmap>> = emptyList()
) : CoilTransformation, Transformation<Bitmap> {

    override val cacheKey: String
        get() = (imageInfo to preset to imageManager to transformations).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val transformedInput =
            imageManager.resize(input, imageInfo.width, imageInfo.height, ResizeType.Flexible)!!
        var info = imageInfo
        if (!preset.isEmpty()) {
            val currentInfo = info.copy()
            info = imageManager.applyPresetBy(
                image = transformedInput,
                preset = preset,
                currentInfo = info
            ).let {
                if (it.quality != currentInfo.quality) {
                    it.copy(quality = currentInfo.quality)
                } else it
            }
        }
        return imageManager.createPreview(
            image = transformedInput,
            imageInfo = info,
            transformations = transformations,
            onGetByteCount = {}
        )
    }
}