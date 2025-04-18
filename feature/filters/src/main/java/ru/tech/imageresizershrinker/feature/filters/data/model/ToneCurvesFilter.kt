package ru.tech.imageresizershrinker.feature.filters.data.model

import android.content.Context
import com.t8rin.curves.GPUImageToneCurveFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.ToneCurvesParams

internal class ToneCurvesFilter(
    private val context: Context,
    override val value: ToneCurvesParams = ToneCurvesParams.Default,
) : GPUFilterTransformation(context), Filter.ToneCurves {

    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageToneCurveFilter(value.controlPoints)
}