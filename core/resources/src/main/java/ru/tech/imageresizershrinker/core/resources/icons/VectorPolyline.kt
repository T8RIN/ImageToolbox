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

val Icons.Outlined.VectorPolyline: ImageVector by lazy {
    ImageVector.Builder(
        name = "VectorPolyline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(2f, 3f)
            verticalLineTo(9f)
            horizontalLineTo(4.95f)
            lineTo(6.95f, 15f)
            horizontalLineTo(6f)
            verticalLineTo(21f)
            horizontalLineTo(12f)
            verticalLineTo(16.41f)
            lineTo(17.41f, 11f)
            horizontalLineTo(22f)
            verticalLineTo(5f)
            horizontalLineTo(16f)
            verticalLineTo(9.57f)
            lineTo(10.59f, 15f)
            horizontalLineTo(9.06f)
            lineTo(7.06f, 9f)
            horizontalLineTo(8f)
            verticalLineTo(3f)
            moveTo(4f, 5f)
            horizontalLineTo(6f)
            verticalLineTo(7f)
            horizontalLineTo(4f)
            moveTo(18f, 7f)
            horizontalLineTo(20f)
            verticalLineTo(9f)
            horizontalLineTo(18f)
            moveTo(8f, 17f)
            horizontalLineTo(10f)
            verticalLineTo(19f)
            horizontalLineTo(8f)
            close()
        }
    }.build()
}