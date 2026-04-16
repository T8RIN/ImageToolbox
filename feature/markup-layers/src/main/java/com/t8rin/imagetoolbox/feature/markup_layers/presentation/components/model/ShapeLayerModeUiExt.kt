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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FreeArrow
import com.t8rin.imagetoolbox.core.resources.icons.FreeDoubleArrow
import com.t8rin.imagetoolbox.core.resources.icons.Line
import com.t8rin.imagetoolbox.core.resources.icons.LineArrow
import com.t8rin.imagetoolbox.core.resources.icons.LineDoubleArrow
import com.t8rin.imagetoolbox.core.resources.icons.Polygon
import com.t8rin.imagetoolbox.core.resources.icons.Square
import com.t8rin.imagetoolbox.core.resources.icons.Triangle
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ShapeMode

internal val ShapeMode.Kind.titleRes: Int
    get() = when (this) {
        ShapeMode.Kind.Line -> R.string.line
        ShapeMode.Kind.Arrow -> R.string.arrow
        ShapeMode.Kind.DoubleArrow -> R.string.double_arrow
        ShapeMode.Kind.LineArrow -> R.string.line_arrow
        ShapeMode.Kind.DoubleLineArrow -> R.string.double_line_arrow
        ShapeMode.Kind.Rect -> R.string.rect
        ShapeMode.Kind.OutlinedRect -> R.string.outlined_rect
        ShapeMode.Kind.Oval -> R.string.oval
        ShapeMode.Kind.OutlinedOval -> R.string.outlined_oval
        ShapeMode.Kind.Triangle -> R.string.triangle
        ShapeMode.Kind.OutlinedTriangle -> R.string.outlined_triangle
        ShapeMode.Kind.Polygon -> R.string.polygon
        ShapeMode.Kind.OutlinedPolygon -> R.string.outlined_polygon
        ShapeMode.Kind.Star -> R.string.star
        ShapeMode.Kind.OutlinedStar -> R.string.outlined_star
    }

internal val ShapeMode.Kind.icon: ImageVector
    get() = when (this) {
        ShapeMode.Kind.Line -> Icons.Rounded.Line
        ShapeMode.Kind.Arrow -> Icons.Rounded.FreeArrow
        ShapeMode.Kind.DoubleArrow -> Icons.Rounded.FreeDoubleArrow
        ShapeMode.Kind.LineArrow -> Icons.Rounded.LineArrow
        ShapeMode.Kind.DoubleLineArrow -> Icons.Rounded.LineDoubleArrow
        ShapeMode.Kind.Rect -> Icons.Rounded.Square
        ShapeMode.Kind.OutlinedRect -> Icons.Rounded.CheckBoxOutlineBlank
        ShapeMode.Kind.Oval -> Icons.Rounded.Circle
        ShapeMode.Kind.OutlinedOval -> Icons.Rounded.RadioButtonUnchecked
        ShapeMode.Kind.Triangle -> Icons.Rounded.Triangle
        ShapeMode.Kind.OutlinedTriangle -> Icons.Outlined.Triangle
        ShapeMode.Kind.Polygon -> Icons.Rounded.Polygon
        ShapeMode.Kind.OutlinedPolygon -> Icons.Outlined.Polygon
        ShapeMode.Kind.Star -> Icons.Rounded.Star
        ShapeMode.Kind.OutlinedStar -> Icons.Rounded.StarOutline
    }
