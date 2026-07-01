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

@UiFilterInject(group = UiFilterInject.Groups.DISTORTION)
class UiRippleFilter(
    override val value: Quad<Float, Float, Float, Float> = 5f to 16f qto (5f to 16f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.ripple,
    paramsInfo = listOf(
        FilterParam(R.string.x_amplitude, 0f..100f, 1),
        FilterParam(R.string.x_wavelength, 1f..200f, 1),
        FilterParam(R.string.y_amplitude, 0f..100f, 1),
        FilterParam(R.string.y_wavelength, 1f..200f, 1)
    ),
    value = value
), Filter.Ripple
