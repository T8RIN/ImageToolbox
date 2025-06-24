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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ImageOverlay: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.ImageOverlay",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(3.5f, 1.5f)
            curveToRelative(-1.11f, 0f, -2f, 0.89f, -2f, 2f)
            verticalLineToRelative(11f)
            curveToRelative(0f, 1.11f, 0.89f, 2f, 2f, 2f)
            curveToRelative(3.67f, 0f, 7.33f, 0f, 11f, 0f)
            curveToRelative(1.11f, 0f, 2f, -0.89f, 2f, -2f)
            curveToRelative(0f, -3.67f, 0f, -7.33f, 0f, -11f)
            curveToRelative(0f, -1.11f, -0.89f, -2f, -2f, -2f)
            horizontalLineTo(3.5f)
            moveTo(3.5f, 3.5f)
            horizontalLineToRelative(11f)
            verticalLineToRelative(11f)
            horizontalLineToRelative(-11f)
            verticalLineTo(3.5f)
            moveTo(18.5f, 7.5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(11f)
            horizontalLineToRelative(-11f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 1.11f, 0.89f, 2f, 2f, 2f)
            horizontalLineToRelative(11f)
            curveToRelative(1.11f, 0f, 2f, -0.89f, 2f, -2f)
            verticalLineToRelative(-11f)
            curveToRelative(0f, -1.11f, -0.89f, -2f, -2f, -2f)
            horizontalLineTo(18.5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(10.633f, 9.242f)
            lineToRelative(-2.292f, 2.95f)
            lineToRelative(-1.633f, -1.967f)
            lineToRelative(-2.292f, 2.942f)
            lineToRelative(9.167f, 0f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageOverlay: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ImageOverlay",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(3.5f, 1.5f)
            curveToRelative(-1.11f, 0f, -2f, 0.89f, -2f, 2f)
            verticalLineToRelative(11f)
            curveToRelative(0f, 1.11f, 0.89f, 2f, 2f, 2f)
            horizontalLineToRelative(11f)
            curveToRelative(1.11f, 0f, 2f, -0.89f, 2f, -2f)
            verticalLineTo(3.5f)
            curveToRelative(0f, -1.11f, -0.89f, -2f, -2f, -2f)
            horizontalLineTo(3.5f)
            moveTo(3.5f, 3.5f)
            horizontalLineToRelative(11f)
            verticalLineToRelative(11f)
            horizontalLineTo(3.5f)
            verticalLineTo(3.5f)
            moveTo(18.5f, 7.5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(11f)
            horizontalLineToRelative(-11f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 1.11f, 0.89f, 2f, 2f, 2f)
            horizontalLineToRelative(11f)
            curveToRelative(1.11f, 0f, 2f, -0.89f, 2f, -2f)
            verticalLineToRelative(-11f)
            curveToRelative(0f, -1.11f, -0.89f, -2f, -2f, -2f)
            horizontalLineToRelative(-2f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(3.5f, 3.5f)
            horizontalLineToRelative(11f)
            verticalLineToRelative(11f)
            horizontalLineToRelative(-11f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(10.633f, 9.242f)
            lineToRelative(-2.292f, 2.95f)
            lineToRelative(-1.633f, -1.967f)
            lineToRelative(-2.292f, 2.942f)
            horizontalLineToRelative(9.167f)
            lineToRelative(-2.95f, -3.925f)
            close()
        }
    }.build()
}