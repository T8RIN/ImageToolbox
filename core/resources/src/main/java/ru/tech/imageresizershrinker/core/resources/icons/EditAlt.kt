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

val Icons.Rounded.EditAlt: ImageVector by lazy {
    ImageVector.Builder(
        name = "Edit Alt", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
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
            moveTo(200.0f, 760.0f)
            horizontalLineToRelative(57.0f)
            lineToRelative(391.0f, -391.0f)
            lineToRelative(-57.0f, -57.0f)
            lineToRelative(-391.0f, 391.0f)
            verticalLineToRelative(57.0f)
            close()
            moveTo(120.0f, 840.0f)
            verticalLineToRelative(-170.0f)
            lineToRelative(528.0f, -527.0f)
            quadToRelative(12.0f, -11.0f, 26.5f, -17.0f)
            reflectiveQuadToRelative(30.5f, -6.0f)
            quadToRelative(16.0f, 0.0f, 31.0f, 6.0f)
            reflectiveQuadToRelative(26.0f, 18.0f)
            lineToRelative(55.0f, 56.0f)
            quadToRelative(12.0f, 11.0f, 17.5f, 26.0f)
            reflectiveQuadToRelative(5.5f, 30.0f)
            quadToRelative(0.0f, 16.0f, -5.5f, 30.5f)
            reflectiveQuadTo(817.0f, 313.0f)
            lineTo(290.0f, 840.0f)
            lineTo(120.0f, 840.0f)
            close()
            moveTo(760.0f, 256.0f)
            lineTo(704.0f, 200.0f)
            lineTo(760.0f, 256.0f)
            close()
            moveTo(619.0f, 341.0f)
            lineTo(591.0f, 312.0f)
            lineTo(648.0f, 369.0f)
            lineTo(619.0f, 341.0f)
            close()
        }
    }.build()
}