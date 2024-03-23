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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Slider: ImageVector by lazy {
    Builder(
        name = "Slider", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(200.0f, 600.0f)
            quadToRelative(-50.0f, 0.0f, -85.0f, -35.0f)
            reflectiveQuadToRelative(-35.0f, -85.0f)
            quadToRelative(0.0f, -50.0f, 35.0f, -85.0f)
            reflectiveQuadToRelative(85.0f, -35.0f)
            horizontalLineToRelative(560.0f)
            quadToRelative(50.0f, 0.0f, 85.0f, 35.0f)
            reflectiveQuadToRelative(35.0f, 85.0f)
            quadToRelative(0.0f, 50.0f, -35.0f, 85.0f)
            reflectiveQuadToRelative(-85.0f, 35.0f)
            lineTo(200.0f, 600.0f)
            close()
            moveTo(560.0f, 520.0f)
            horizontalLineToRelative(200.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, -11.5f)
            reflectiveQuadTo(800.0f, 480.0f)
            quadToRelative(0.0f, -17.0f, -11.5f, -28.5f)
            reflectiveQuadTo(760.0f, 440.0f)
            lineTo(560.0f, 440.0f)
            verticalLineToRelative(80.0f)
            close()
        }
    }
        .build()
}