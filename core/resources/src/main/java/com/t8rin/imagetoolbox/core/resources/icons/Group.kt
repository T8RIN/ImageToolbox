/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Group: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Group",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(1f, 1f)
            verticalLineTo(5f)
            horizontalLineTo(2f)
            verticalLineTo(19f)
            horizontalLineTo(1f)
            verticalLineTo(23f)
            horizontalLineTo(5f)
            verticalLineTo(22f)
            horizontalLineTo(19f)
            verticalLineTo(23f)
            horizontalLineTo(23f)
            verticalLineTo(19f)
            horizontalLineTo(22f)
            verticalLineTo(5f)
            horizontalLineTo(23f)
            verticalLineTo(1f)
            horizontalLineTo(19f)
            verticalLineTo(2f)
            horizontalLineTo(5f)
            verticalLineTo(1f)
            moveTo(5f, 4f)
            horizontalLineTo(19f)
            verticalLineTo(5f)
            horizontalLineTo(20f)
            verticalLineTo(19f)
            horizontalLineTo(19f)
            verticalLineTo(20f)
            horizontalLineTo(5f)
            verticalLineTo(19f)
            horizontalLineTo(4f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            moveTo(6f, 6f)
            verticalLineTo(14f)
            horizontalLineTo(9f)
            verticalLineTo(18f)
            horizontalLineTo(18f)
            verticalLineTo(9f)
            horizontalLineTo(14f)
            verticalLineTo(6f)
            moveTo(8f, 8f)
            horizontalLineTo(12f)
            verticalLineTo(12f)
            horizontalLineTo(8f)
            moveTo(14f, 11f)
            horizontalLineTo(16f)
            verticalLineTo(16f)
            horizontalLineTo(11f)
            verticalLineTo(14f)
            horizontalLineTo(14f)
        }
    }.build()
}
