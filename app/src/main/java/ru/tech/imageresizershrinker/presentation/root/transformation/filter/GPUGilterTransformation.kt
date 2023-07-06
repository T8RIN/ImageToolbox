package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

abstract class GPUFilterTransformation(
    private val context: Context
) : Transformation {

    /**
     * Create the [GPUImageFilter] to apply to this [Transformation]
     */
    abstract fun createFilter(): GPUImageFilter

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val gpuImage = GPUImage(context)
        gpuImage.setImage(input)
        gpuImage.setFilter(createFilter())
        return gpuImage.bitmapWithFilterApplied
    }
}