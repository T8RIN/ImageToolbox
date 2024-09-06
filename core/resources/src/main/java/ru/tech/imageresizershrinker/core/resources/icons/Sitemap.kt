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

val Icons.Outlined.Sitemap by lazy {
    ImageVector.Builder(
        name = "Sitemap Outlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(21f, 16f)
            verticalLineTo(13f)
            curveTo(21f, 11.89f, 20.11f, 11f, 19f, 11f)
            horizontalLineTo(13f)
            verticalLineTo(8f)
            horizontalLineTo(15f)
            verticalLineTo(2f)
            horizontalLineTo(9f)
            verticalLineTo(8f)
            horizontalLineTo(11f)
            verticalLineTo(11f)
            horizontalLineTo(5f)
            curveTo(3.89f, 11f, 3f, 11.89f, 3f, 13f)
            verticalLineTo(16f)
            horizontalLineTo(1f)
            verticalLineTo(22f)
            horizontalLineTo(7f)
            verticalLineTo(16f)
            horizontalLineTo(5f)
            verticalLineTo(13f)
            horizontalLineTo(11f)
            verticalLineTo(16f)
            horizontalLineTo(9f)
            verticalLineTo(22f)
            horizontalLineTo(15f)
            verticalLineTo(16f)
            horizontalLineTo(13f)
            verticalLineTo(13f)
            horizontalLineTo(19f)
            verticalLineTo(16f)
            horizontalLineTo(17f)
            verticalLineTo(22f)
            horizontalLineTo(23f)
            verticalLineTo(16f)
            horizontalLineTo(21f)
            moveTo(11f, 4f)
            horizontalLineTo(13f)
            verticalLineTo(6f)
            horizontalLineTo(11f)
            verticalLineTo(4f)
            moveTo(5f, 20f)
            horizontalLineTo(3f)
            verticalLineTo(18f)
            horizontalLineTo(5f)
            verticalLineTo(20f)
            moveTo(13f, 20f)
            horizontalLineTo(11f)
            verticalLineTo(18f)
            horizontalLineTo(13f)
            verticalLineTo(20f)
            moveTo(21f, 20f)
            horizontalLineTo(19f)
            verticalLineTo(18f)
            horizontalLineTo(21f)
            verticalLineTo(20f)
            close()
        }
    }.build()
}