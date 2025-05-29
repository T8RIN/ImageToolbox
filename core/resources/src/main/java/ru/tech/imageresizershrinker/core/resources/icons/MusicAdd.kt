/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

val Icons.Rounded.MusicAdd: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MusicAdd",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(17f, 9f)
            verticalLineTo(12f)
            horizontalLineTo(14f)
            verticalLineTo(14f)
            horizontalLineTo(17f)
            verticalLineTo(17f)
            horizontalLineTo(19f)
            verticalLineTo(14f)
            horizontalLineTo(22f)
            verticalLineTo(12f)
            horizontalLineTo(19f)
            verticalLineTo(9f)
            horizontalLineTo(17f)
            moveTo(9f, 3f)
            verticalLineTo(13.55f)
            curveTo(8.41f, 13.21f, 7.73f, 13f, 7f, 13f)
            curveTo(4.79f, 13f, 3f, 14.79f, 3f, 17f)
            reflectiveCurveTo(4.79f, 21f, 7f, 21f)
            reflectiveCurveTo(11f, 19.21f, 11f, 17f)
            verticalLineTo(7f)
            horizontalLineTo(15f)
            verticalLineTo(3f)
            horizontalLineTo(9f)
            close()
        }
    }.build()
}
