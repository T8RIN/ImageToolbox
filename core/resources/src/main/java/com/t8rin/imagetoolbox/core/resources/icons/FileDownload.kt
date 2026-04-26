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

val Icons.Rounded.FileDownload: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FileDownload",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 15.575f)
            quadToRelative(-0.2f, 0f, -0.375f, -0.063f)
            quadToRelative(-0.175f, -0.062f, -0.325f, -0.212f)
            lineToRelative(-3.6f, -3.6f)
            quadToRelative(-0.275f, -0.275f, -0.275f, -0.7f)
            quadToRelative(0f, -0.425f, 0.275f, -0.7f)
            quadToRelative(0.275f, -0.275f, 0.712f, -0.288f)
            quadToRelative(0.438f, -0.012f, 0.713f, 0.263f)
            lineTo(11f, 12.15f)
            verticalLineTo(5f)
            quadToRelative(0f, -0.425f, 0.288f, -0.713f)
            quadTo(11.575f, 4f, 12f, 4f)
            reflectiveQuadToRelative(0.713f, 0.287f)
            quadTo(13f, 4.575f, 13f, 5f)
            verticalLineToRelative(7.15f)
            lineToRelative(1.875f, -1.875f)
            quadToRelative(0.275f, -0.275f, 0.713f, -0.263f)
            quadToRelative(0.437f, 0.013f, 0.712f, 0.288f)
            quadToRelative(0.275f, 0.275f, 0.275f, 0.7f)
            quadToRelative(0f, 0.425f, -0.275f, 0.7f)
            lineToRelative(-3.6f, 3.6f)
            quadToRelative(-0.15f, 0.15f, -0.325f, 0.212f)
            quadToRelative(-0.175f, 0.063f, -0.375f, 0.063f)
            close()
            moveTo(6f, 20f)
            quadToRelative(-0.825f, 0f, -1.412f, -0.587f)
            quadTo(4f, 18.825f, 4f, 18f)
            verticalLineToRelative(-2f)
            quadToRelative(0f, -0.425f, 0.287f, -0.713f)
            quadTo(4.575f, 15f, 5f, 15f)
            reflectiveQuadToRelative(0.713f, 0.287f)
            quadTo(6f, 15.575f, 6f, 16f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(-2f)
            quadToRelative(0f, -0.425f, 0.288f, -0.713f)
            quadTo(18.575f, 15f, 19f, 15f)
            reflectiveQuadToRelative(0.712f, 0.287f)
            quadTo(20f, 15.575f, 20f, 16f)
            verticalLineToRelative(2f)
            quadToRelative(0f, 0.825f, -0.587f, 1.413f)
            quadTo(18.825f, 20f, 18f, 20f)
            close()
        }
    }.build()
}
