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

val Icons.Rounded.GooglePlay: ImageVector by lazy {
    ImageVector.Builder(
        name = "Google Play", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
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
            moveTo(3.0f, 20.5f)
            verticalLineTo(3.5f)
            curveTo(3.0f, 2.91f, 3.34f, 2.39f, 3.84f, 2.15f)
            lineTo(13.69f, 12.0f)
            lineTo(3.84f, 21.85f)
            curveTo(3.34f, 21.6f, 3.0f, 21.09f, 3.0f, 20.5f)
            moveTo(16.81f, 15.12f)
            lineTo(6.05f, 21.34f)
            lineTo(14.54f, 12.85f)
            lineTo(16.81f, 15.12f)
            moveTo(20.16f, 10.81f)
            curveTo(20.5f, 11.08f, 20.75f, 11.5f, 20.75f, 12.0f)
            curveTo(20.75f, 12.5f, 20.53f, 12.9f, 20.18f, 13.18f)
            lineTo(17.89f, 14.5f)
            lineTo(15.39f, 12.0f)
            lineTo(17.89f, 9.5f)
            lineTo(20.16f, 10.81f)
            moveTo(6.05f, 2.66f)
            lineTo(16.81f, 8.88f)
            lineTo(14.54f, 11.15f)
            lineTo(6.05f, 2.66f)
            close()
        }
    }.build()
}