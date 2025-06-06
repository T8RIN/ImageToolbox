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

val Icons.Outlined.MeshGradient: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.MeshGradient",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(5.6f, 21f)
            horizontalLineToRelative(5.1f)
            horizontalLineToRelative(2.6f)
            horizontalLineToRelative(5.1f)
            curveToRelative(1.4f, 0f, 2.6f, -1.2f, 2.6f, -2.6f)
            verticalLineTo(5.6f)
            curveTo(21f, 4.2f, 19.8f, 3f, 18.4f, 3f)
            horizontalLineTo(5.6f)
            curveTo(4.2f, 3f, 3f, 4.2f, 3f, 5.6f)
            verticalLineToRelative(12.9f)
            curveTo(3f, 19.8f, 4.2f, 21f, 5.6f, 21f)
            close()
            moveTo(18.4f, 18.4f)
            horizontalLineToRelative(-2f)
            curveToRelative(0f, -0.1f, -0.7f, -1.6f, -0.7f, -1.7f)
            curveToRelative(0.9f, -0.1f, 1.8f, -0.4f, 2.6f, -0.8f)
            lineToRelative(0f, 0f)
            curveToRelative(0f, 0f, 0.1f, 0f, 0.1f, -0.1f)
            verticalLineTo(18.4f)
            close()
            moveTo(18.4f, 5.6f)
            verticalLineToRelative(2f)
            lineToRelative(0f, 0f)
            curveToRelative(-0.1f, 0f, -0.6f, 0.2f, -0.6f, 0.2f)
            curveToRelative(-0.2f, 0.1f, -0.4f, 0.1f, -0.5f, 0.2f)
            curveToRelative(-0.2f, 0.1f, -0.4f, 0.2f, -0.5f, 0.3f)
            curveToRelative(-0.1f, -1f, -0.4f, -1.9f, -0.9f, -2.7f)
            curveToRelative(0f, 0f, 0f, 0f, 0f, -0.1f)
            horizontalLineTo(18.4f)
            close()
            moveTo(15.8f, 12.8f)
            curveToRelative(0.3f, -0.6f, 0.6f, -1.2f, 0.8f, -1.9f)
            curveToRelative(0.7f, -0.2f, 1.3f, -0.5f, 1.8f, -0.7f)
            verticalLineToRelative(3.1f)
            lineToRelative(-0.2f, 0.1f)
            curveToRelative(-0.3f, 0.1f, -0.6f, 0.2f, -0.9f, 0.4f)
            curveToRelative(-0.5f, 0.3f, -1f, 0.5f, -1.5f, 0.6f)
            curveToRelative(-0.1f, 0f, -0.3f, 0f, -0.4f, 0f)
            curveTo(15.5f, 13.8f, 15.6f, 13.3f, 15.8f, 12.8f)
            close()
            moveTo(13.3f, 5.6f)
            lineToRelative(0f, 0.1f)
            curveToRelative(0f, 0f, 0f, 0.1f, 0f, 0.1f)
            lineToRelative(0.1f, 0.4f)
            lineToRelative(0.2f, 0.4f)
            lineToRelative(0f, 0f)
            curveToRelative(0f, 0f, 0f, 0f, 0f, 0.1f)
            lineToRelative(0f, 0f)
            curveToRelative(0.3f, 0.5f, 0.5f, 1f, 0.6f, 1.4f)
            curveToRelative(0f, 0.2f, 0f, 0.4f, 0.1f, 0.6f)
            curveToRelative(-0.5f, -0.1f, -1.1f, -0.2f, -1.6f, -0.4f)
            curveToRelative(-0.6f, -0.3f, -1.2f, -0.6f, -1.9f, -0.7f)
            curveToRelative(-0.2f, -0.7f, -0.5f, -1.3f, -0.8f, -1.9f)
            horizontalLineTo(13.3f)
            close()
            moveTo(10.3f, 12.5f)
            curveToRelative(0.4f, -0.8f, 0.7f, -1.6f, 0.7f, -2.4f)
            curveToRelative(0.2f, 0.1f, 0.5f, 0.2f, 0.8f, 0.4f)
            curveToRelative(0.7f, 0.3f, 1.4f, 0.5f, 2.2f, 0.6f)
            curveToRelative(-0.2f, 0.4f, -0.4f, 0.7f, -0.5f, 1.1f)
            curveToRelative(-0.3f, 0.6f, -0.4f, 1.2f, -0.5f, 1.8f)
            curveToRelative(-0.3f, -0.1f, -0.6f, -0.3f, -0.9f, -0.4f)
            lineToRelative(0f, 0f)
            curveToRelative(-0.7f, -0.3f, -1.4f, -0.5f, -2.1f, -0.6f)
            curveTo(10.1f, 12.8f, 10.2f, 12.7f, 10.3f, 12.5f)
            close()
            moveTo(11.3f, 15.8f)
            lineTo(11.3f, 15.8f)
            curveToRelative(0.5f, 0.2f, 0.9f, 0.5f, 1.5f, 0.6f)
            curveToRelative(0.1f, 0f, 0.2f, 0.1f, 0.3f, 0.1f)
            curveToRelative(0.2f, 0.6f, 0.4f, 1.2f, 0.7f, 1.7f)
            verticalLineToRelative(0f)
            curveToRelative(0f, 0f, 0f, 0.1f, 0.1f, 0.2f)
            horizontalLineToRelative(-3.1f)
            lineToRelative(-0.1f, -0.2f)
            curveToRelative(-0.1f, -0.3f, -0.2f, -0.6f, -0.4f, -0.9f)
            lineToRelative(0f, 0f)
            curveToRelative(-0.2f, -0.5f, -0.4f, -0.9f, -0.5f, -1.3f)
            curveToRelative(0f, -0.2f, 0f, -0.4f, -0.1f, -0.6f)
            curveTo(10.3f, 15.4f, 10.8f, 15.6f, 11.3f, 15.8f)
            close()
            moveTo(5.6f, 5.6f)
            horizontalLineToRelative(2f)
            lineToRelative(0.3f, 0.6f)
            curveTo(8f, 6.4f, 8f, 6.7f, 8.2f, 6.9f)
            curveTo(8.2f, 7f, 8.3f, 7.2f, 8.4f, 7.3f)
            curveTo(7.5f, 7.4f, 6.7f, 7.7f, 5.9f, 8.1f)
            horizontalLineToRelative(0f)
            curveToRelative(-0.1f, 0f, -0.2f, 0.1f, -0.3f, 0.1f)
            verticalLineTo(5.6f)
            close()
            moveTo(5.6f, 10.8f)
            lineToRelative(0.2f, -0.1f)
            curveToRelative(0.3f, -0.1f, 0.6f, -0.2f, 0.9f, -0.4f)
            lineToRelative(0f, 0f)
            curveToRelative(0.5f, -0.3f, 1f, -0.5f, 1.4f, -0.6f)
            horizontalLineToRelative(0f)
            curveToRelative(0.2f, 0f, 0.4f, 0f, 0.6f, -0.1f)
            curveToRelative(0f, 0.5f, -0.2f, 1f, -0.4f, 1.5f)
            curveToRelative(-0.3f, 0.6f, -0.6f, 1.2f, -0.8f, 1.9f)
            curveToRelative(-0.7f, 0.2f, -1.4f, 0.5f, -1.9f, 0.8f)
            verticalLineTo(10.8f)
            close()
            moveTo(5.6f, 16.5f)
            lineToRelative(0.2f, -0.1f)
            curveToRelative(0.4f, -0.1f, 0.8f, -0.3f, 1.2f, -0.5f)
            horizontalLineToRelative(0f)
            curveToRelative(0.1f, -0.1f, 0.3f, -0.1f, 0.4f, -0.2f)
            curveToRelative(0.1f, 0.9f, 0.4f, 1.7f, 0.8f, 2.5f)
            verticalLineToRelative(0f)
            curveToRelative(0f, 0.1f, 0.1f, 0.2f, 0.1f, 0.2f)
            horizontalLineTo(5.6f)
            verticalLineTo(16.5f)
            close()
        }
    }.build()
}
