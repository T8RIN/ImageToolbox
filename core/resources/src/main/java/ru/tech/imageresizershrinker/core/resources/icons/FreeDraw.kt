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

val Icons.Rounded.FreeDraw: ImageVector by lazy {
    Builder(
        name = "FreeDraw", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(554.0f, 840.0f)
            quadToRelative(-54.0f, 0.0f, -91.0f, -37.0f)
            reflectiveQuadToRelative(-37.0f, -89.0f)
            quadToRelative(0.0f, -76.0f, 61.5f, -137.5f)
            reflectiveQuadTo(641.0f, 500.0f)
            quadToRelative(-3.0f, -36.0f, -18.0f, -54.5f)
            reflectiveQuadTo(582.0f, 427.0f)
            quadToRelative(-30.0f, 0.0f, -65.0f, 25.0f)
            reflectiveQuadToRelative(-83.0f, 82.0f)
            quadToRelative(-78.0f, 93.0f, -114.5f, 121.0f)
            reflectiveQuadTo(241.0f, 683.0f)
            quadToRelative(-51.0f, 0.0f, -86.0f, -38.0f)
            reflectiveQuadToRelative(-35.0f, -92.0f)
            quadToRelative(0.0f, -54.0f, 23.5f, -110.5f)
            reflectiveQuadTo(223.0f, 307.0f)
            quadToRelative(19.0f, -26.0f, 28.0f, -44.0f)
            reflectiveQuadToRelative(9.0f, -29.0f)
            quadToRelative(0.0f, -7.0f, -2.5f, -10.5f)
            reflectiveQuadTo(250.0f, 220.0f)
            quadToRelative(-10.0f, 0.0f, -25.0f, 12.5f)
            reflectiveQuadTo(190.0f, 271.0f)
            lineToRelative(-70.0f, -71.0f)
            quadToRelative(32.0f, -39.0f, 65.0f, -59.5f)
            reflectiveQuadToRelative(65.0f, -20.5f)
            quadToRelative(46.0f, 0.0f, 78.0f, 32.0f)
            reflectiveQuadToRelative(32.0f, 80.0f)
            quadToRelative(0.0f, 29.0f, -15.0f, 64.0f)
            reflectiveQuadToRelative(-50.0f, 84.0f)
            quadToRelative(-38.0f, 54.0f, -56.5f, 95.0f)
            reflectiveQuadTo(220.0f, 547.0f)
            quadToRelative(0.0f, 17.0f, 5.5f, 26.5f)
            reflectiveQuadTo(241.0f, 583.0f)
            quadToRelative(10.0f, 0.0f, 17.5f, -5.5f)
            reflectiveQuadTo(286.0f, 551.0f)
            quadToRelative(13.0f, -14.0f, 31.0f, -34.5f)
            reflectiveQuadToRelative(44.0f, -50.5f)
            quadToRelative(63.0f, -75.0f, 114.0f, -107.0f)
            reflectiveQuadToRelative(107.0f, -32.0f)
            quadToRelative(67.0f, 0.0f, 110.0f, 45.0f)
            reflectiveQuadToRelative(49.0f, 123.0f)
            horizontalLineToRelative(99.0f)
            verticalLineToRelative(100.0f)
            horizontalLineToRelative(-99.0f)
            quadToRelative(-8.0f, 112.0f, -58.5f, 178.5f)
            reflectiveQuadTo(554.0f, 840.0f)
            close()
            moveTo(556.0f, 740.0f)
            quadToRelative(32.0f, 0.0f, 54.0f, -36.5f)
            reflectiveQuadTo(640.0f, 602.0f)
            quadToRelative(-46.0f, 11.0f, -80.0f, 43.5f)
            reflectiveQuadTo(526.0f, 710.0f)
            quadToRelative(0.0f, 14.0f, 8.0f, 22.0f)
            reflectiveQuadToRelative(22.0f, 8.0f)
            close()
        }
    }.build()
}