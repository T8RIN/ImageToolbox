/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.filters.presentation.model

import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel

class UiContourFilter(
    override val value: Quad<Float, Float, Float, ColorModel> = Quad(
        first = 5f,
        second = 1f,
        third = 0f,
        fourth = Color(0xff000000).toModel()
    )
) : UiFilter<Quad<Float, Float, Float, ColorModel>>(
    title = R.string.contour,
    paramsInfo = listOf(
        FilterParam(R.string.levels, 0f..50f, 2),
        FilterParam(R.string.scale, 0f..1f, 0),
        FilterParam(R.string.offset, 0f..255f, 0),
        FilterParam(R.string.color, 0f..0f, 0),
    ),
    value = value
), Filter.Contour