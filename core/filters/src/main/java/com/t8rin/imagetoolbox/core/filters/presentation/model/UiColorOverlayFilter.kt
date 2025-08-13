/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValueWrapper
import com.t8rin.imagetoolbox.core.filters.domain.model.wrap
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel


class UiColorOverlayFilter(
    override val value: FilterValueWrapper<ColorModel> = Color.Yellow.copy(0.3f).toModel().wrap(),
) : UiFilter<FilterValueWrapper<ColorModel>>(
    title = R.string.color_filter,
    value = value
), Filter.ColorOverlay