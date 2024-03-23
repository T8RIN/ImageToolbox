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

val Icons.Rounded.ImageReset: ImageVector by lazy {
    Builder(
        name = "ImageReset", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(120.0f, 360.0f)
            verticalLineToRelative(-240.0f)
            horizontalLineToRelative(80.0f)
            verticalLineToRelative(134.0f)
            quadToRelative(50.0f, -62.0f, 122.5f, -98.0f)
            reflectiveQuadTo(480.0f, 120.0f)
            quadToRelative(118.0f, 0.0f, 210.5f, 67.0f)
            reflectiveQuadTo(820.0f, 360.0f)
            horizontalLineToRelative(-87.0f)
            quadToRelative(-34.0f, -72.0f, -101.0f, -116.0f)
            reflectiveQuadToRelative(-152.0f, -44.0f)
            quadToRelative(-57.0f, 0.0f, -107.5f, 21.0f)
            reflectiveQuadTo(284.0f, 280.0f)
            horizontalLineToRelative(76.0f)
            verticalLineToRelative(80.0f)
            lineTo(120.0f, 360.0f)
            close()
            moveTo(240.0f, 720.0f)
            horizontalLineToRelative(480.0f)
            lineTo(570.0f, 520.0f)
            lineTo(450.0f, 680.0f)
            lineToRelative(-90.0f, -120.0f)
            lineToRelative(-120.0f, 160.0f)
            close()
            moveTo(200.0f, 880.0f)
            quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
            reflectiveQuadTo(120.0f, 800.0f)
            verticalLineToRelative(-320.0f)
            horizontalLineToRelative(80.0f)
            verticalLineToRelative(320.0f)
            horizontalLineToRelative(560.0f)
            verticalLineToRelative(-320.0f)
            horizontalLineToRelative(80.0f)
            verticalLineToRelative(320.0f)
            quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
            reflectiveQuadTo(760.0f, 880.0f)
            lineTo(200.0f, 880.0f)
            close()
        }
    }.build()
}