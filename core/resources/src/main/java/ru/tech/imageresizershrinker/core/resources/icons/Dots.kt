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

val Icons.Rounded.Dots: ImageVector by lazy {
    Builder(
        name = "Dots", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.0f, 19.0f)
            curveTo(13.1f, 19.0f, 14.0f, 19.9f, 14.0f, 21.0f)
            reflectiveCurveTo(13.1f, 23.0f, 12.0f, 23.0f)
            reflectiveCurveTo(10.0f, 22.1f, 10.0f, 21.0f)
            reflectiveCurveTo(10.9f, 19.0f, 12.0f, 19.0f)
            moveTo(12.0f, 1.0f)
            curveTo(13.1f, 1.0f, 14.0f, 1.9f, 14.0f, 3.0f)
            reflectiveCurveTo(13.1f, 5.0f, 12.0f, 5.0f)
            reflectiveCurveTo(10.0f, 4.1f, 10.0f, 3.0f)
            reflectiveCurveTo(10.9f, 1.0f, 12.0f, 1.0f)
            moveTo(6.0f, 16.0f)
            curveTo(7.1f, 16.0f, 8.0f, 16.9f, 8.0f, 18.0f)
            reflectiveCurveTo(7.1f, 20.0f, 6.0f, 20.0f)
            reflectiveCurveTo(4.0f, 19.1f, 4.0f, 18.0f)
            reflectiveCurveTo(4.9f, 16.0f, 6.0f, 16.0f)
            moveTo(3.0f, 10.0f)
            curveTo(4.1f, 10.0f, 5.0f, 10.9f, 5.0f, 12.0f)
            reflectiveCurveTo(4.1f, 14.0f, 3.0f, 14.0f)
            reflectiveCurveTo(1.0f, 13.1f, 1.0f, 12.0f)
            reflectiveCurveTo(1.9f, 10.0f, 3.0f, 10.0f)
            moveTo(6.0f, 4.0f)
            curveTo(7.1f, 4.0f, 8.0f, 4.9f, 8.0f, 6.0f)
            reflectiveCurveTo(7.1f, 8.0f, 6.0f, 8.0f)
            reflectiveCurveTo(4.0f, 7.1f, 4.0f, 6.0f)
            reflectiveCurveTo(4.9f, 4.0f, 6.0f, 4.0f)
            moveTo(18.0f, 16.0f)
            curveTo(19.1f, 16.0f, 20.0f, 16.9f, 20.0f, 18.0f)
            reflectiveCurveTo(19.1f, 20.0f, 18.0f, 20.0f)
            reflectiveCurveTo(16.0f, 19.1f, 16.0f, 18.0f)
            reflectiveCurveTo(16.9f, 16.0f, 18.0f, 16.0f)
            moveTo(21.0f, 10.0f)
            curveTo(22.1f, 10.0f, 23.0f, 10.9f, 23.0f, 12.0f)
            reflectiveCurveTo(22.1f, 14.0f, 21.0f, 14.0f)
            reflectiveCurveTo(19.0f, 13.1f, 19.0f, 12.0f)
            reflectiveCurveTo(19.9f, 10.0f, 21.0f, 10.0f)
            moveTo(18.0f, 4.0f)
            curveTo(19.1f, 4.0f, 20.0f, 4.9f, 20.0f, 6.0f)
            reflectiveCurveTo(19.1f, 8.0f, 18.0f, 8.0f)
            reflectiveCurveTo(16.0f, 7.1f, 16.0f, 6.0f)
            reflectiveCurveTo(16.9f, 4.0f, 18.0f, 4.0f)
            close()
        }
    }.build()
}