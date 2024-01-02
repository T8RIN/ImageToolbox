package ru.tech.imageresizershrinker.coredata.image.filters


import android.content.Context
import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSobelEdgeDetectionFilter
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class SobelEdgeDetectionFilter(
    private val context: Context,
    override val value: Float = 1f,
) : GPUFilterTransformation(context), Filter.SobelEdgeDetection<Bitmap> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSobelEdgeDetectionFilter().apply {
        setLineSize(value)
    }
}