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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.qto
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.BLUR)
class UiLensBlurFilter(
    override val value: Quad<Float, Float, Float, Float> = 10f to 5f qto (2f to 255f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.lens_blur,
    paramsInfo = listOf(
        FilterParam(R.string.radius, 0f..50f, 0),
        FilterParam(R.string.sides, 3f..12f, 0),
        R.string.bloom paramTo 0f..10f,
        FilterParam(R.string.threshold, 0f..255f, 0)
    ),
    value = value
), Filter.LensBlur
