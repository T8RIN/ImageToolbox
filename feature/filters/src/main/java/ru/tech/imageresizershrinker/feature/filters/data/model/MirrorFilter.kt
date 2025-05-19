package ru.tech.imageresizershrinker.feature.filters.data.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.MirrorSide
import ru.tech.imageresizershrinker.feature.filters.data.utils.mirror

internal class MirrorFilter(
    override val value: Pair<Float, MirrorSide> = 0.5f to MirrorSide.LeftToRight,
) : Transformation<Bitmap>, Filter.Mirror {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = input.mirror(
        value = value.first,
        side = value.second
    )

}