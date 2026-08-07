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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons


val Icons.Outlined.GridOn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.GridOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(200f, 760f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(200f, 627f)
            verticalLineToRelative(133f)
            close()
            moveTo(413f, 760f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-133f)
            lineTo(413f, 627f)
            verticalLineToRelative(133f)
            close()
            moveTo(627f, 760f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(627f, 627f)
            verticalLineToRelative(133f)
            close()
            moveTo(200f, 547f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-134f)
            lineTo(200f, 413f)
            verticalLineToRelative(134f)
            close()
            moveTo(413f, 547f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-134f)
            lineTo(413f, 413f)
            verticalLineToRelative(134f)
            close()
            moveTo(627f, 547f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-134f)
            lineTo(627f, 413f)
            verticalLineToRelative(134f)
            close()
            moveTo(200f, 333f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(200f, 200f)
            verticalLineToRelative(133f)
            close()
            moveTo(413f, 333f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-133f)
            lineTo(413f, 200f)
            verticalLineToRelative(133f)
            close()
            moveTo(627f, 333f)
            horizontalLineToRelative(133f)
            verticalLineToRelative(-133f)
            lineTo(627f, 200f)
            verticalLineToRelative(133f)
            close()
        }
    }.build()
}

val Icons.TwoTone.GridOn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.GridOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 21f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            curveToRelative(0.392f, -0.392f, 0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            curveToRelative(-0.392f, 0.392f, -0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(5f)
            close()
            moveTo(5f, 19f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(-3.325f)
            horizontalLineToRelative(-3.325f)
            verticalLineToRelative(3.325f)
            close()
            moveTo(10.325f, 19f)
            horizontalLineToRelative(3.35f)
            verticalLineToRelative(-3.325f)
            horizontalLineToRelative(-3.35f)
            verticalLineToRelative(3.325f)
            close()
            moveTo(15.675f, 19f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(-3.325f)
            horizontalLineToRelative(-3.325f)
            verticalLineToRelative(3.325f)
            close()
            moveTo(5f, 13.675f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(-3.35f)
            horizontalLineToRelative(-3.325f)
            verticalLineToRelative(3.35f)
            close()
            moveTo(10.325f, 13.675f)
            horizontalLineToRelative(3.35f)
            verticalLineToRelative(-3.35f)
            horizontalLineToRelative(-3.35f)
            verticalLineToRelative(3.35f)
            close()
            moveTo(15.675f, 13.675f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(-3.35f)
            horizontalLineToRelative(-3.325f)
            verticalLineToRelative(3.35f)
            close()
            moveTo(5f, 8.325f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(-3.325f)
            horizontalLineToRelative(-3.325f)
            verticalLineToRelative(3.325f)
            close()
            moveTo(10.325f, 8.325f)
            horizontalLineToRelative(3.35f)
            verticalLineToRelative(-3.325f)
            horizontalLineToRelative(-3.35f)
            verticalLineToRelative(3.325f)
            close()
            moveTo(15.675f, 8.325f)
            horizontalLineToRelative(3.325f)
            verticalLineToRelative(-3.325f)
            horizontalLineToRelative(-3.325f)
            verticalLineToRelative(3.325f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(-14f)
            close()
        }
    }.build()
}