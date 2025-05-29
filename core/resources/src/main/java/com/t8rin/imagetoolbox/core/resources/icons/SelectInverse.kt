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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.SelectInverse: ImageVector by lazy {
    ImageVector.Builder(
        name = "Rounded.SelectInverse",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(5f, 3f)
            horizontalLineTo(7f)
            verticalLineTo(5f)
            horizontalLineTo(9f)
            verticalLineTo(3f)
            horizontalLineTo(11f)
            verticalLineTo(5f)
            horizontalLineTo(13f)
            verticalLineTo(3f)
            horizontalLineTo(15f)
            verticalLineTo(5f)
            horizontalLineTo(17f)
            verticalLineTo(3f)
            horizontalLineTo(19f)
            verticalLineTo(5f)
            horizontalLineTo(21f)
            verticalLineTo(7f)
            horizontalLineTo(19f)
            verticalLineTo(9f)
            horizontalLineTo(21f)
            verticalLineTo(11f)
            horizontalLineTo(19f)
            verticalLineTo(13f)
            horizontalLineTo(21f)
            verticalLineTo(15f)
            horizontalLineTo(19f)
            verticalLineTo(17f)
            horizontalLineTo(21f)
            verticalLineTo(19f)
            horizontalLineTo(19f)
            verticalLineTo(21f)
            horizontalLineTo(17f)
            verticalLineTo(19f)
            horizontalLineTo(15f)
            verticalLineTo(21f)
            horizontalLineTo(13f)
            verticalLineTo(19f)
            horizontalLineTo(11f)
            verticalLineTo(21f)
            horizontalLineTo(9f)
            verticalLineTo(19f)
            horizontalLineTo(7f)
            verticalLineTo(21f)
            horizontalLineTo(5f)
            verticalLineTo(19f)
            horizontalLineTo(3f)
            verticalLineTo(17f)
            horizontalLineTo(5f)
            verticalLineTo(15f)
            horizontalLineTo(3f)
            verticalLineTo(13f)
            horizontalLineTo(5f)
            verticalLineTo(11f)
            horizontalLineTo(3f)
            verticalLineTo(9f)
            horizontalLineTo(5f)
            verticalLineTo(7f)
            horizontalLineTo(3f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            verticalLineTo(3f)
            close()
        }
    }.build()
}
