package ru.tech.imageresizershrinker.core.data.image.filters

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class RGBFilter(
    private val context: Context,
    override val value: Color = Color.White,
) : GPUFilterTransformation(context), Filter.RGB<Bitmap, Color> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageRGBFilter(
        value.red, value.green, value.blue
    )
}