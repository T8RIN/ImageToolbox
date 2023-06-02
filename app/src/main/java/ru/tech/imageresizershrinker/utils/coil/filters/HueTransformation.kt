package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import com.commit451.coiltransformations.gpu.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter

class HueTransformation(
    private val context: Context,
    val hue: Float = 90f,
) : GPUFilterTransformation(context) {
    override val cacheKey: String
        get() = (hue to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageHueFilter(hue)
}

fun Context.HueTransformation(
    hue: Float = 90f
) = HueTransformation(this, hue)