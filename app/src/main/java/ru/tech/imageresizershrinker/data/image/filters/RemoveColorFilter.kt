package ru.tech.imageresizershrinker.data.image.filters

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import coil.size.Size
import ru.tech.imageresizershrinker.coredomain.image.Transformation
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter

class RemoveColorFilter(
    override val value: Pair<Float, Color> = 0f to Color(0xFF000000),
) : Filter.RemoveColor<Bitmap, Color>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap = ReplaceColorFilter(
        value = Triple(
            first = value.first,
            second = value.second,
            third = Color.Transparent
        )
    ).transform(input, size)
}