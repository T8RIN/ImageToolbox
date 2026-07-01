/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.jhlabs.JhFilter
import com.jhlabs.LevelsFilter
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.qto
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterInject
internal class LevelsFilter(
    override val value: Quad<Float, Float, Float, Float> = 0f to 1f qto (0f to 1f)
) : JhFilterTransformation(), Filter.Levels {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): JhFilter = LevelsFilter().apply {
        lowLevel = value.first
        highLevel = value.second
        lowOutputLevel = value.third
        highOutputLevel = value.fourth
    }

}
