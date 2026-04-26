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

val Icons.Rounded.CompareArrows: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.CompareArrows",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(367f, 640f)
            lineTo(120f, 640f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 600f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 560f)
            horizontalLineToRelative(247f)
            lineToRelative(-75f, -75f)
            quadToRelative(-11f, -11f, -11f, -27.5f)
            reflectiveQuadToRelative(11f, -28.5f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            lineToRelative(143f, 143f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineTo(348f, 772f)
            quadToRelative(-12f, 12f, -28f, 11.5f)
            reflectiveQuadTo(292f, 771f)
            quadToRelative(-11f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(11.5f, -28f)
            lineToRelative(75f, -75f)
            close()
            moveTo(593f, 400f)
            lineTo(668f, 475f)
            quadToRelative(11f, 11f, 11f, 27.5f)
            reflectiveQuadTo(668f, 531f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(611f, 531f)
            lineTo(468f, 388f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadToRelative(-2.5f, -15f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(144f, -144f)
            quadToRelative(12f, -12f, 28f, -11.5f)
            reflectiveQuadToRelative(28f, 12.5f)
            quadToRelative(11f, 12f, 11.5f, 28f)
            reflectiveQuadTo(668f, 245f)
            lineToRelative(-75f, 75f)
            horizontalLineToRelative(247f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 360f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 400f)
            lineTo(593f, 400f)
            close()
        }
    }.build()
}
