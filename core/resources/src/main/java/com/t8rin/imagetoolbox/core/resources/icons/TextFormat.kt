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

val Icons.Rounded.TextFormat: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.TextFormat",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(240f, 760f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(200f, 720f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(240f, 680f)
            horizontalLineToRelative(480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(760f, 720f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(720f, 760f)
            lineTo(240f, 760f)
            close()
            moveTo(294f, 552f)
            lineTo(431f, 184f)
            quadToRelative(4f, -11f, 13.5f, -17.5f)
            reflectiveQuadTo(466f, 160f)
            horizontalLineToRelative(28f)
            quadToRelative(12f, 0f, 21.5f, 6.5f)
            reflectiveQuadTo(529f, 184f)
            lineToRelative(137f, 369f)
            quadToRelative(6f, 17f, -4f, 32f)
            reflectiveQuadToRelative(-28f, 15f)
            quadToRelative(-11f, 0f, -20.5f, -6.5f)
            reflectiveQuadTo(600f, 576f)
            lineToRelative(-30f, -88f)
            lineTo(392f, 488f)
            lineToRelative(-32f, 89f)
            quadToRelative(-4f, 11f, -13f, 17f)
            reflectiveQuadToRelative(-20f, 6f)
            quadToRelative(-19f, 0f, -29.5f, -15.5f)
            reflectiveQuadTo(294f, 552f)
            close()
            moveTo(414f, 424f)
            horizontalLineToRelative(132f)
            lineToRelative(-64f, -182f)
            horizontalLineToRelative(-4f)
            lineToRelative(-64f, 182f)
            close()
        }
    }.build()
}
