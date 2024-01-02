package ru.tech.imageresizershrinker.coredata.image.filters

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFalseColorFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class FalseColorFilter(
    private val context: Context,
    override val value: Pair<Color, Color> = Color(
        red = 0f,
        green = 0f,
        blue = 0.5f
    ) to Color(
        red = 1f,
        green = 0f,
        blue = 0f
    ),
) : GPUFilterTransformation(context), Filter.FalseColor<Bitmap, Color> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageFalseColorFilter(
        value.first.red,
        value.first.green,
        value.first.blue,
        value.second.red,
        value.second.green,
        value.second.blue
    )
}