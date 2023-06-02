package ru.tech.imageresizershrinker.utils.coil.filters


import android.content.Context
import com.commit451.coiltransformations.gpu.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter

class SaturationTransformation(
    private val context: Context,
    val saturation: Float = 1f,
) : GPUFilterTransformation(context) {
    override val cacheKey: String
        get() = (saturation to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSaturationFilter(saturation)
}

fun Context.SaturationTransformation(
    saturation: Float = 1f
) = SaturationTransformation(this, saturation)