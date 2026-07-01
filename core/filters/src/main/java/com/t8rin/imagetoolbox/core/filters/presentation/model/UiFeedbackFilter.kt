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
class UiFeedbackFilter(
    override val value: Quad<Float, Float, Float, Float> = 10f to 0f qto (3f to 0.05f)
) : UiFilter<Quad<Float, Float, Float, Float>>(
    title = R.string.feedback,
    paramsInfo = listOf(
        FilterParam(R.string.distance, -100f..100f, 0),
        FilterParam(R.string.angle, -180f..180f, 0),
        FilterParam(R.string.rotation, -180f..180f, 0),
        R.string.zoom paramTo -1f..1f
    ),
    value = value
), Filter.Feedback
