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

val Icons.Outlined.MaterialDesign: ImageVector by lazy {
    ImageVector.Builder(
        name = "MaterialDesign",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(21f, 12f)
            curveTo(21f, 9.97f, 20.33f, 8.09f, 19f, 6.38f)
            verticalLineTo(17.63f)
            curveTo(20.33f, 15.97f, 21f, 14.09f, 21f, 12f)
            moveTo(17.63f, 19f)
            horizontalLineTo(6.38f)
            curveTo(7.06f, 19.55f, 7.95f, 20f, 9.05f, 20.41f)
            curveTo(10.14f, 20.8f, 11.13f, 21f, 12f, 21f)
            curveTo(12.88f, 21f, 13.86f, 20.8f, 14.95f, 20.41f)
            curveTo(16.05f, 20f, 16.94f, 19.55f, 17.63f, 19f)
            moveTo(11f, 17f)
            lineTo(7f, 9f)
            verticalLineTo(17f)
            horizontalLineTo(11f)
            moveTo(17f, 9f)
            lineTo(13f, 17f)
            horizontalLineTo(17f)
            verticalLineTo(9f)
            moveTo(12f, 14.53f)
            lineTo(15.75f, 7f)
            horizontalLineTo(8.25f)
            lineTo(12f, 14.53f)
            moveTo(17.63f, 5f)
            curveTo(15.97f, 3.67f, 14.09f, 3f, 12f, 3f)
            curveTo(9.91f, 3f, 8.03f, 3.67f, 6.38f, 5f)
            horizontalLineTo(17.63f)
            moveTo(5f, 17.63f)
            verticalLineTo(6.38f)
            curveTo(3.67f, 8.09f, 3f, 9.97f, 3f, 12f)
            curveTo(3f, 14.09f, 3.67f, 15.97f, 5f, 17.63f)
            moveTo(23f, 12f)
            curveTo(23f, 15.03f, 21.94f, 17.63f, 19.78f, 19.78f)
            curveTo(17.63f, 21.94f, 15.03f, 23f, 12f, 23f)
            curveTo(8.97f, 23f, 6.38f, 21.94f, 4.22f, 19.78f)
            curveTo(2.06f, 17.63f, 1f, 15.03f, 1f, 12f)
            curveTo(1f, 8.97f, 2.06f, 6.38f, 4.22f, 4.22f)
            curveTo(6.38f, 2.06f, 8.97f, 1f, 12f, 1f)
            curveTo(15.03f, 1f, 17.63f, 2.06f, 19.78f, 4.22f)
            curveTo(21.94f, 6.38f, 23f, 8.97f, 23f, 12f)
            close()
        }
    }.build()
}
