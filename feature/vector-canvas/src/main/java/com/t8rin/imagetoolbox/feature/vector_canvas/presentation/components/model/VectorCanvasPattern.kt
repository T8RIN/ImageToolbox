/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

@file:Suppress("PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.feature.vector_canvas.R
import com.t8rin.imagetoolbox.core.resources.R as CoreR

enum class VectorCanvasPattern(
    @StringRes val title: Int,
    @DrawableRes val drawable: Int?
) {
    None(CoreR.string.vector_canvas_pattern_none, null),
    Grid(CoreR.string.vector_canvas_pattern_grid, R.drawable.vector_canvas_pattern_grid),
    Checkers(
        CoreR.string.vector_canvas_pattern_checkers,
        R.drawable.vector_canvas_pattern_checkers
    ),
    Crosses(CoreR.string.vector_canvas_pattern_crosses, R.drawable.vector_canvas_pattern_crosses),
    Texture(CoreR.string.vector_canvas_pattern_texture, R.drawable.vector_canvas_pattern_texture)
}
