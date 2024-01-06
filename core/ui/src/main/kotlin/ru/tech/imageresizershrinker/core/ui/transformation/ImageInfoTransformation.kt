package ru.tech.imageresizershrinker.core.ui.transformation

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import coil.size.Size
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.Preset
import ru.tech.imageresizershrinker.core.domain.model.ResizeType
import coil.transform.Transformation as CoilTransformation

class ImageInfoTransformation(
    private val imageInfo: ImageInfo,
    private val preset: Preset = Preset.Numeric(100),
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val transformations: List<Transformation<Bitmap>> = emptyList()
) : CoilTransformation, Transformation<Bitmap> {

    constructor(
        filters: List<Filter<Bitmap, *>>,
        imageInfo: ImageInfo,
        preset: Preset = Preset.Numeric(100),
        imageManager: ImageManager<Bitmap, ExifInterface>,
    ) : this(
        imageInfo = imageInfo,
        preset = preset,
        imageManager = imageManager,
        transformations = filters.map {
            imageManager.getFilterProvider().filterToTransformation(it)
        }
    )

    override val cacheKey: String
        get() = (imageInfo to preset to imageManager to transformations).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val transformedInput =
            imageManager.resize(
                image = input,
                width = imageInfo.width,
                height = imageInfo.height,
                resizeType = ResizeType.Flexible,
                imageScaleMode = ImageScaleMode.NotPresent
            )!!
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