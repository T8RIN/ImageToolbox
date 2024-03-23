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

val Icons.Outlined.Encrypted: ImageVector by lazy {
    ImageVector.Builder(
        name = "Encrypted", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
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
            moveTo(420.0f, 600.0f)
            horizontalLineToRelative(120.0f)
            lineToRelative(-23.0f, -129.0f)
            quadToRelative(20.0f, -10.0f, 31.5f, -29.0f)
            reflectiveQuadToRelative(11.5f, -42.0f)
            quadToRelative(0.0f, -33.0f, -23.5f, -56.5f)
            reflectiveQuadTo(480.0f, 320.0f)
            quadToRelative(-33.0f, 0.0f, -56.5f, 23.5f)
            reflectiveQuadTo(400.0f, 400.0f)
            quadToRelative(0.0f, 23.0f, 11.5f, 42.0f)
            reflectiveQuadToRelative(31.5f, 29.0f)
            lineToRelative(-23.0f, 129.0f)
            close()
            moveTo(480.0f, 880.0f)
            quadToRelative(-139.0f, -35.0f, -229.5f, -159.5f)
            reflectiveQuadTo(160.0f, 444.0f)
            verticalLineToRelative(-244.0f)
            lineToRelative(320.0f, -120.0f)
            lineToRelative(320.0f, 120.0f)
            verticalLineToRelative(244.0f)
            quadToRelative(0.0f, 152.0f, -90.5f, 276.5f)
            reflectiveQuadTo(480.0f, 880.0f)
            close()
            moveTo(480.0f, 796.0f)
            quadToRelative(104.0f, -33.0f, 172.0f, -132.0f)
            reflectiveQuadToRelative(68.0f, -220.0f)
            verticalLineToRelative(-189.0f)
            lineToRelative(-240.0f, -90.0f)
            lineToRelative(-240.0f, 90.0f)
            verticalLineToRelative(189.0f)
            quadToRelative(0.0f, 121.0f, 68.0f, 220.0f)
            reflectiveQuadToRelative(172.0f, 132.0f)
            close()
            moveTo(480.0f, 480.0f)
            close()
        }
    }.build()
}