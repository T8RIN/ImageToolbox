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

val Icons.Rounded.Business: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Business",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(2f, 19f)
            verticalLineTo(5f)
            quadTo(2f, 4.175f, 2.588f, 3.588f)
            quadTo(3.175f, 3f, 4f, 3f)
            horizontalLineTo(10f)
            quadTo(10.825f, 3f, 11.413f, 3.588f)
            quadTo(12f, 4.175f, 12f, 5f)
            verticalLineTo(7f)
            horizontalLineTo(20f)
            quadTo(20.825f, 7f, 21.413f, 7.588f)
            quadTo(22f, 8.175f, 22f, 9f)
            verticalLineTo(19f)
            quadTo(22f, 19.825f, 21.413f, 20.413f)
            quadTo(20.825f, 21f, 20f, 21f)
            horizontalLineTo(4f)
            quadTo(3.175f, 21f, 2.588f, 20.413f)
            quadTo(2f, 19.825f, 2f, 19f)
            close()
            moveTo(4f, 19f)
            horizontalLineTo(6f)
            verticalLineTo(17f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 15f)
            horizontalLineTo(6f)
            verticalLineTo(13f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 11f)
            horizontalLineTo(6f)
            verticalLineTo(9f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 7f)
            horizontalLineTo(6f)
            verticalLineTo(5f)
            horizontalLineTo(4f)
            close()
            moveTo(8f, 19f)
            horizontalLineTo(10f)
            verticalLineTo(17f)
            horizontalLineTo(8f)
            close()
            moveTo(8f, 15f)
            horizontalLineTo(10f)
            verticalLineTo(13f)
            horizontalLineTo(8f)
            close()
            moveTo(8f, 11f)
            horizontalLineTo(10f)
            verticalLineTo(9f)
            horizontalLineTo(8f)
            close()
            moveTo(8f, 7f)
            horizontalLineTo(10f)
            verticalLineTo(5f)
            horizontalLineTo(8f)
            close()
            moveTo(12f, 19f)
            horizontalLineTo(20f)
            verticalLineTo(9f)
            horizontalLineTo(12f)
            verticalLineTo(11f)
            horizontalLineTo(14f)
            verticalLineTo(13f)
            horizontalLineTo(12f)
            verticalLineTo(15f)
            horizontalLineTo(14f)
            verticalLineTo(17f)
            horizontalLineTo(12f)
            close()
            moveTo(16f, 13f)
            verticalLineTo(11f)
            horizontalLineTo(18f)
            verticalLineTo(13f)
            close()
            moveTo(16f, 17f)
            verticalLineTo(15f)
            horizontalLineTo(18f)
            verticalLineTo(17f)
            close()
        }
    }.build()
}
