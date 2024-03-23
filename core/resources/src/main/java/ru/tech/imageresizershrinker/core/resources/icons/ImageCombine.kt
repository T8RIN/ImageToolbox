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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.ImageCombine: ImageVector by lazy {
    ImageVector.Builder(
        name = "Image Combine", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(160.0f, 360.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(120.0f, 320.0f)
            verticalLineToRelative(-120.0f)
            quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
            reflectiveQuadTo(200.0f, 120.0f)
            horizontalLineToRelative(120.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(360.0f, 160.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(320.0f, 200.0f)
            lineTo(200.0f, 200.0f)
            verticalLineToRelative(120.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(160.0f, 360.0f)
            close()
            moveTo(800.0f, 360.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(760.0f, 320.0f)
            verticalLineToRelative(-120.0f)
            lineTo(640.0f, 200.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(600.0f, 160.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(640.0f, 120.0f)
            horizontalLineToRelative(120.0f)
            quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
            reflectiveQuadTo(840.0f, 200.0f)
            verticalLineToRelative(120.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(800.0f, 360.0f)
            close()
            moveTo(645.0f, 605.0f)
            lineToRelative(-97.0f, -97.0f)
            quadToRelative(-12.0f, -12.0f, -12.0f, -28.0f)
            reflectiveQuadToRelative(12.0f, -28.0f)
            lineToRelative(97.0f, -97.0f)
            quadToRelative(12.0f, -12.0f, 28.5f, -12.0f)
            reflectiveQuadToRelative(28.5f, 12.0f)
            quadToRelative(12.0f, 12.0f, 12.0f, 28.5f)
            reflectiveQuadTo(702.0f, 412.0f)
            lineToRelative(-29.0f, 28.0f)
            horizontalLineToRelative(167.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(880.0f, 480.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(840.0f, 520.0f)
            lineTo(673.0f, 520.0f)
            lineToRelative(29.0f, 28.0f)
            quadToRelative(12.0f, 12.0f, 12.0f, 28.5f)
            reflectiveQuadTo(702.0f, 605.0f)
            quadToRelative(-12.0f, 12.0f, -28.5f, 12.0f)
            reflectiveQuadTo(645.0f, 605.0f)
            close()
            moveTo(259.0f, 605.0f)
            quadToRelative(-12.0f, -12.0f, -12.5f, -28.5f)
            reflectiveQuadTo(258.0f, 548.0f)
            lineToRelative(29.0f, -28.0f)
            lineTo(120.0f, 520.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(80.0f, 480.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(120.0f, 440.0f)
            horizontalLineToRelative(167.0f)
            lineToRelative(-29.0f, -28.0f)
            quadToRelative(-12.0f, -12.0f, -11.5f, -28.5f)
            reflectiveQuadTo(259.0f, 355.0f)
            quadToRelative(12.0f, -12.0f, 28.0f, -12.0f)
            reflectiveQuadToRelative(28.0f, 12.0f)
            lineToRelative(97.0f, 97.0f)
            quadToRelative(12.0f, 12.0f, 12.0f, 28.0f)
            reflectiveQuadToRelative(-12.0f, 28.0f)
            lineToRelative(-97.0f, 97.0f)
            quadToRelative(-12.0f, 12.0f, -28.0f, 12.0f)
            reflectiveQuadToRelative(-28.0f, -12.0f)
            close()
            moveTo(200.0f, 840.0f)
            quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
            reflectiveQuadTo(120.0f, 760.0f)
            verticalLineToRelative(-120.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(160.0f, 600.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(200.0f, 640.0f)
            verticalLineToRelative(120.0f)
            horizontalLineToRelative(120.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(360.0f, 800.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(320.0f, 840.0f)
            lineTo(200.0f, 840.0f)
            close()
            moveTo(640.0f, 840.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(600.0f, 800.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(640.0f, 760.0f)
            horizontalLineToRelative(120.0f)
            verticalLineToRelative(-120.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(800.0f, 600.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(840.0f, 640.0f)
            verticalLineToRelative(120.0f)
            quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
            reflectiveQuadTo(760.0f, 840.0f)
            lineTo(640.0f, 840.0f)
            close()
        }
    }.build()
}