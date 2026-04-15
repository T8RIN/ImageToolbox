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
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode

internal val DrawPathMode.titleRes: Int
    get() = when (this) {
        DrawPathMode.Line -> R.string.line
        is DrawPathMode.PointingArrow -> R.string.arrow
        is DrawPathMode.DoublePointingArrow -> R.string.double_arrow
        is DrawPathMode.LinePointingArrow -> R.string.line_arrow
        is DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow
        is DrawPathMode.Rect -> R.string.rect
        is DrawPathMode.OutlinedRect -> R.string.outlined_rect
        DrawPathMode.Oval -> R.string.oval
        is DrawPathMode.OutlinedOval -> R.string.outlined_oval
        DrawPathMode.Triangle -> R.string.triangle
        is DrawPathMode.OutlinedTriangle -> R.string.outlined_triangle
        is DrawPathMode.Polygon -> R.string.polygon
        is DrawPathMode.OutlinedPolygon -> R.string.outlined_polygon
        is DrawPathMode.Star -> R.string.star
        is DrawPathMode.OutlinedStar -> R.string.outlined_star
        else -> R.string.shape
    }

internal val DrawPathMode.subtitleRes: Int?
    get() = when (this) {
        DrawPathMode.Line -> R.string.line_sub
        is DrawPathMode.PointingArrow -> R.string.arrow_sub
        is DrawPathMode.DoublePointingArrow -> R.string.double_arrow_sub
        is DrawPathMode.LinePointingArrow -> R.string.line_arrow_sub
        is DrawPathMode.DoubleLinePointingArrow -> R.string.double_line_arrow_sub
        is DrawPathMode.OutlinedRect -> R.string.outlined_rect_sub
        is DrawPathMode.OutlinedOval -> R.string.outlined_oval_sub
        is DrawPathMode.OutlinedTriangle -> R.string.outlined_triangle_sub
        is DrawPathMode.OutlinedPolygon -> R.string.outlined_polygon_sub
        is DrawPathMode.OutlinedStar -> R.string.outlined_star_sub
        else -> null
    }

internal val DrawPathMode.icon: ImageVector
    get() = when (this) {
        DrawPathMode.Line -> Icons.Rounded.Line
        is DrawPathMode.PointingArrow -> Icons.Rounded.FreeArrow
        is DrawPathMode.DoublePointingArrow -> Icons.Rounded.FreeDoubleArrow
        is DrawPathMode.LinePointingArrow -> Icons.Rounded.LineArrow
        is DrawPathMode.DoubleLinePointingArrow -> Icons.Rounded.LineDoubleArrow
        is DrawPathMode.Rect -> Icons.Rounded.Square
        is DrawPathMode.OutlinedRect -> Icons.Rounded.CheckBoxOutlineBlank
        DrawPathMode.Oval -> Icons.Rounded.Circle
        is DrawPathMode.OutlinedOval -> Icons.Rounded.RadioButtonUnchecked
        DrawPathMode.Triangle -> Icons.Rounded.Triangle
        is DrawPathMode.OutlinedTriangle -> Icons.Outlined.Triangle
        is DrawPathMode.Polygon -> Icons.Rounded.Polygon
        is DrawPathMode.OutlinedPolygon -> Icons.Outlined.Polygon
        is DrawPathMode.Star -> Icons.Rounded.Star
        is DrawPathMode.OutlinedStar -> Icons.Rounded.StarOutline
        else -> Icons.Rounded.Square
    }
