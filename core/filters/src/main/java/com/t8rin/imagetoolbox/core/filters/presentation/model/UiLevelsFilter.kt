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
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.LIGHT)
class UiLevelsFilter(
    override val value: Quad<Float, Float, Float, Float> = 0f to 1f qto (0f to 1f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.levels,
    paramsInfo = listOf(
        R.string.low_input paramTo 0f..1f,
        R.string.high_input paramTo 0f..1f,
        R.string.low_output paramTo 0f..1f,
        R.string.high_output paramTo 0f..1f
    ),
    value = value
), Filter.Levels
