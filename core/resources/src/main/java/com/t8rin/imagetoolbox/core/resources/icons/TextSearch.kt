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

val Icons.Outlined.TextSearch: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.TextSearch",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.31f, 18.9f)
            lineTo(22.39f, 22f)
            lineTo(21f, 23.39f)
            lineTo(17.88f, 20.32f)
            curveTo(17.19f, 20.75f, 16.37f, 21f, 15.5f, 21f)
            curveTo(13f, 21f, 11f, 19f, 11f, 16.5f)
            curveTo(11f, 14f, 13f, 12f, 15.5f, 12f)
            curveTo(18f, 12f, 20f, 14f, 20f, 16.5f)
            curveTo(20f, 17.38f, 19.75f, 18.21f, 19.31f, 18.9f)
            moveTo(15.5f, 19f)
            curveTo(16.88f, 19f, 18f, 17.88f, 18f, 16.5f)
            curveTo(18f, 15.12f, 16.88f, 14f, 15.5f, 14f)
            curveTo(14.12f, 14f, 13f, 15.12f, 13f, 16.5f)
            curveTo(13f, 17.88f, 14.12f, 19f, 15.5f, 19f)
            moveTo(21f, 4f)
            verticalLineTo(6f)
            horizontalLineTo(3f)
            verticalLineTo(4f)
            horizontalLineTo(21f)
            moveTo(3f, 16f)
            verticalLineTo(14f)
            horizontalLineTo(9f)
            verticalLineTo(16f)
            horizontalLineTo(3f)
            moveTo(3f, 11f)
            verticalLineTo(9f)
            horizontalLineTo(21f)
            verticalLineTo(11f)
            horizontalLineTo(18.97f)
            curveTo(17.96f, 10.37f, 16.77f, 10f, 15.5f, 10f)
            curveTo(14.23f, 10f, 13.04f, 10.37f, 12.03f, 11f)
            horizontalLineTo(3f)
            close()
        }
    }.build()
}
