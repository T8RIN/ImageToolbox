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

val Icons.Rounded.Highlighter: ImageVector by lazy {
    Builder(
        name = "Highlighter", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveToRelative(396.0f, 396.0f)
            lineToRelative(200.0f, 200.0f)
            lineToRelative(-212.0f, 212.0f)
            quadToRelative(-19.0f, 19.0f, -52.5f, 19.0f)
            reflectiveQuadTo(279.0f, 808.0f)
            lineToRelative(-11.0f, -11.0f)
            lineToRelative(-43.0f, 43.0f)
            lineTo(70.0f, 840.0f)
            lineToRelative(120.0f, -120.0f)
            lineToRelative(-4.0f, -4.0f)
            quadToRelative(-22.0f, -22.0f, -21.5f, -55.5f)
            reflectiveQuadTo(187.0f, 605.0f)
            lineToRelative(209.0f, -209.0f)
            close()
            moveTo(439.0f, 353.0f)
            lineTo(655.0f, 137.0f)
            quadToRelative(17.0f, -17.0f, 43.0f, -17.0f)
            reflectiveQuadToRelative(43.0f, 17.0f)
            lineToRelative(112.0f, 112.0f)
            quadToRelative(17.0f, 17.0f, 16.5f, 45.5f)
            reflectiveQuadTo(852.0f, 340.0f)
            lineTo(639.0f, 553.0f)
            lineTo(439.0f, 353.0f)
            close()
        }
    }.build()
}