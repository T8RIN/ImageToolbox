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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ScaleUnbalanced: ImageVector by lazy {
    ImageVector.Builder(
        name = "ScaleUnbalanced",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(13f, 20f)
            verticalLineTo(8.8f)
            curveTo(13.5f, 8.6f, 14f, 8.3f, 14.3f, 7.9f)
            lineTo(17.8f, 9.2f)
            lineTo(14.9f, 16f)
            curveTo(14.4f, 18f, 15.9f, 19f, 18.4f, 19f)
            reflectiveCurveTo(22.5f, 18f, 21.9f, 16f)
            lineTo(19.3f, 9.7f)
            lineTo(20.2f, 10f)
            lineTo(20.9f, 8.1f)
            lineTo(15f, 6f)
            curveTo(15f, 4.8f, 14.3f, 3.6f, 13f, 3.1f)
            curveTo(11.8f, 2.6f, 10.5f, 3.1f, 9.7f, 4f)
            lineTo(3.9f, 2f)
            lineTo(3.2f, 3.8f)
            lineTo(4.8f, 4.4f)
            lineTo(2.1f, 11f)
            curveTo(1.6f, 13f, 3.1f, 14f, 5.6f, 14f)
            reflectiveCurveTo(9.7f, 13f, 9.1f, 11f)
            lineTo(6.6f, 5.1f)
            lineTo(9f, 6f)
            curveTo(9f, 7.2f, 9.7f, 8.4f, 11f, 8.9f)
            verticalLineTo(20f)
            horizontalLineTo(2f)
            verticalLineTo(22f)
            horizontalLineTo(22f)
            verticalLineTo(20f)
            horizontalLineTo(13f)
            moveTo(19.9f, 16f)
            horizontalLineTo(16.9f)
            lineTo(18.4f, 12.2f)
            lineTo(19.9f, 16f)
            moveTo(7.1f, 11f)
            horizontalLineTo(4.1f)
            lineTo(5.6f, 7.2f)
            lineTo(7.1f, 11f)
            moveTo(11.1f, 5.7f)
            curveTo(11.3f, 5.2f, 11.9f, 4.9f, 12.4f, 5.1f)
            reflectiveCurveTo(13.2f, 5.9f, 13f, 6.4f)
            reflectiveCurveTo(12.2f, 7.2f, 11.7f, 7f)
            reflectiveCurveTo(10.9f, 6.2f, 11.1f, 5.7f)
            close()
        }
    }.build()
}
