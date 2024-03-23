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

val Icons.Outlined.Puzzle: ImageVector by lazy {
    Builder(
        name = "Puzzle", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(349.0f, 859.0f)
            lineTo(174.0f, 859.0f)
            quadToRelative(-28.725f, 0.0f, -50.863f, -22.137f)
            quadTo(101.0f, 814.725f, 101.0f, 786.0f)
            verticalLineToRelative(-169.0f)
            quadToRelative(46.0f, -5.0f, 78.0f, -37.5f)
            reflectiveQuadToRelative(32.0f, -78.5f)
            quadToRelative(0.0f, -46.0f, -32.0f, -78.5f)
            reflectiveQuadTo(101.0f, 385.0f)
            verticalLineToRelative(-170.0f)
            quadToRelative(0.0f, -28.725f, 22.137f, -50.862f)
            quadTo(145.275f, 142.0f, 174.0f, 142.0f)
            horizontalLineToRelative(172.0f)
            quadToRelative(11.0f, -43.0f, 41.958f, -72.5f)
            quadToRelative(30.957f, -29.5f, 73.0f, -29.5f)
            quadTo(503.0f, 40.0f, 534.0f, 69.68f)
            reflectiveQuadTo(576.0f, 142.0f)
            horizontalLineToRelative(169.0f)
            quadToRelative(28.725f, 0.0f, 50.862f, 22.138f)
            quadTo(818.0f, 186.275f, 818.0f, 215.0f)
            verticalLineToRelative(169.0f)
            quadToRelative(43.0f, 11.0f, 71.0f, 43.958f)
            quadToRelative(28.0f, 32.957f, 28.0f, 75.0f)
            quadTo(917.0f, 545.0f, 888.82f, 574.5f)
            reflectiveQuadTo(818.0f, 615.0f)
            verticalLineToRelative(171.0f)
            quadToRelative(0.0f, 28.725f, -22.138f, 50.863f)
            quadTo(773.725f, 859.0f, 745.0f, 859.0f)
            lineTo(569.0f, 859.0f)
            quadToRelative(-2.0f, -53.0f, -33.75f, -86.5f)
            reflectiveQuadTo(459.0f, 739.0f)
            quadToRelative(-44.5f, 0.0f, -76.25f, 33.5f)
            reflectiveQuadTo(349.0f, 859.0f)
            close()
            moveTo(174.0f, 786.0f)
            horizontalLineToRelative(116.0f)
            quadToRelative(24.0f, -62.0f, 71.388f, -91.0f)
            quadToRelative(47.388f, -29.0f, 97.5f, -29.0f)
            reflectiveQuadTo(557.0f, 695.0f)
            quadToRelative(48.0f, 29.0f, 73.0f, 91.0f)
            horizontalLineToRelative(115.0f)
            verticalLineToRelative(-237.0f)
            horizontalLineToRelative(53.0f)
            quadToRelative(20.0f, 0.0f, 33.0f, -13.0f)
            reflectiveQuadToRelative(13.0f, -33.0f)
            quadToRelative(0.0f, -20.0f, -13.0f, -33.0f)
            reflectiveQuadToRelative(-33.0f, -13.0f)
            horizontalLineToRelative(-53.0f)
            verticalLineToRelative(-242.0f)
            lineTo(507.0f, 215.0f)
            verticalLineToRelative(-56.0f)
            quadToRelative(0.0f, -20.0f, -13.0f, -33.0f)
            reflectiveQuadToRelative(-33.0f, -13.0f)
            quadToRelative(-20.0f, 0.0f, -33.0f, 13.0f)
            reflectiveQuadToRelative(-13.0f, 33.0f)
            verticalLineToRelative(56.0f)
            lineTo(174.0f, 215.0f)
            verticalLineToRelative(115.0f)
            quadToRelative(49.15f, 24.817f, 79.575f, 70.186f)
            quadTo(284.0f, 445.555f, 284.0f, 501.223f)
            quadToRelative(0.0f, 54.929f, -30.5f, 100.353f)
            quadTo(223.0f, 647.0f, 174.0f, 671.0f)
            verticalLineToRelative(115.0f)
            close()
            moveTo(461.0f, 503.0f)
            close()
        }
    }.build()
}