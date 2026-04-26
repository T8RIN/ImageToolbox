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

val Icons.Rounded.FormatUnderlined: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FormatUnderlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(240f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(200f, 800f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(240f, 760f)
            horizontalLineToRelative(480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(760f, 800f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(720f, 840f)
            lineTo(240f, 840f)
            close()
            moveTo(480f, 680f)
            quadToRelative(-101f, 0f, -157f, -63f)
            reflectiveQuadToRelative(-56f, -167f)
            verticalLineToRelative(-279f)
            quadToRelative(0f, -21f, 15.5f, -36f)
            reflectiveQuadToRelative(36.5f, -15f)
            quadToRelative(21f, 0f, 36f, 15f)
            reflectiveQuadToRelative(15f, 36f)
            verticalLineToRelative(285f)
            quadToRelative(0f, 56f, 28f, 91f)
            reflectiveQuadToRelative(82f, 35f)
            quadToRelative(54f, 0f, 82f, -35f)
            reflectiveQuadToRelative(28f, -91f)
            verticalLineToRelative(-285f)
            quadToRelative(0f, -21f, 15.5f, -36f)
            reflectiveQuadToRelative(36.5f, -15f)
            quadToRelative(21f, 0f, 36f, 15f)
            reflectiveQuadToRelative(15f, 36f)
            verticalLineToRelative(279f)
            quadToRelative(0f, 104f, -56f, 167f)
            reflectiveQuadToRelative(-157f, 63f)
            close()
        }
    }.build()
}
