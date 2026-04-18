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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Ungroup: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Ungroup",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(2f, 2f)
            horizontalLineTo(6f)
            verticalLineTo(3f)
            horizontalLineTo(13f)
            verticalLineTo(2f)
            horizontalLineTo(17f)
            verticalLineTo(6f)
            horizontalLineTo(16f)
            verticalLineTo(9f)
            horizontalLineTo(18f)
            verticalLineTo(8f)
            horizontalLineTo(22f)
            verticalLineTo(12f)
            horizontalLineTo(21f)
            verticalLineTo(18f)
            horizontalLineTo(22f)
            verticalLineTo(22f)
            horizontalLineTo(18f)
            verticalLineTo(21f)
            horizontalLineTo(12f)
            verticalLineTo(22f)
            horizontalLineTo(8f)
            verticalLineTo(18f)
            horizontalLineTo(9f)
            verticalLineTo(16f)
            horizontalLineTo(6f)
            verticalLineTo(17f)
            horizontalLineTo(2f)
            verticalLineTo(13f)
            horizontalLineTo(3f)
            verticalLineTo(6f)
            horizontalLineTo(2f)
            verticalLineTo(2f)
            moveTo(18f, 12f)
            verticalLineTo(11f)
            horizontalLineTo(16f)
            verticalLineTo(13f)
            horizontalLineTo(17f)
            verticalLineTo(17f)
            horizontalLineTo(13f)
            verticalLineTo(16f)
            horizontalLineTo(11f)
            verticalLineTo(18f)
            horizontalLineTo(12f)
            verticalLineTo(19f)
            horizontalLineTo(18f)
            verticalLineTo(18f)
            horizontalLineTo(19f)
            verticalLineTo(12f)
            horizontalLineTo(18f)
            moveTo(13f, 6f)
            verticalLineTo(5f)
            horizontalLineTo(6f)
            verticalLineTo(6f)
            horizontalLineTo(5f)
            verticalLineTo(13f)
            horizontalLineTo(6f)
            verticalLineTo(14f)
            horizontalLineTo(9f)
            verticalLineTo(12f)
            horizontalLineTo(8f)
            verticalLineTo(8f)
            horizontalLineTo(12f)
            verticalLineTo(9f)
            horizontalLineTo(14f)
            verticalLineTo(6f)
            horizontalLineTo(13f)
            moveTo(12f, 12f)
            horizontalLineTo(11f)
            verticalLineTo(14f)
            horizontalLineTo(13f)
            verticalLineTo(13f)
            horizontalLineTo(14f)
            verticalLineTo(11f)
            horizontalLineTo(12f)
            verticalLineTo(12f)
            close()
        }
    }.build()
}
