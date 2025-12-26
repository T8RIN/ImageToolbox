package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.utils.glitch.GlitchTool
import kotlin.math.roundToInt

@FilterInject
internal class PixelMeltFilter(
    override val value: Pair<Float, Float> = 20f to 0.5f,
) : Transformation<Bitmap>, Filter.PixelMelt {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = GlitchTool.pixelMelt(
        src = input,
        maxDrop = value.first.roundToInt(),
        strength = value.second
    )

}