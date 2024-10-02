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

val Icons.Outlined.FileReplace: ImageVector by lazy {
    ImageVector.Builder(
        name = "FileReplace",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14f, 3f)
            lineTo(12f, 1f)
            horizontalLineTo(4f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 3f)
            verticalLineTo(15f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4f, 17f)
            horizontalLineTo(11f)
            verticalLineTo(19f)
            lineTo(15f, 16f)
            lineTo(11f, 13f)
            verticalLineTo(15f)
            horizontalLineTo(4f)
            verticalLineTo(3f)
            horizontalLineTo(14f)
            moveTo(21f, 10f)
            verticalLineTo(21f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 23f)
            horizontalLineTo(8f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 21f)
            verticalLineTo(19f)
            horizontalLineTo(8f)
            verticalLineTo(21f)
            horizontalLineTo(19f)
            verticalLineTo(12f)
            horizontalLineTo(14f)
            verticalLineTo(7f)
            horizontalLineTo(8f)
            verticalLineTo(13f)
            horizontalLineTo(6f)
            verticalLineTo(7f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 5f)
            horizontalLineTo(16f)
            lineTo(21f, 10f)
            close()
        }
    }.build()
}
