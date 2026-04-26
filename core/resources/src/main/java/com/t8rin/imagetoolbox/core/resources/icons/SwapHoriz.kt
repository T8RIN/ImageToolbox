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

val Icons.Rounded.SwapHoriz: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.SwapHoriz",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(233f, 640f)
            lineToRelative(75f, 75f)
            quadToRelative(11f, 11f, 11f, 27.5f)
            reflectiveQuadTo(308f, 771f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(251f, 771f)
            lineTo(108f, 628f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadTo(97f, 600f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(144f, -144f)
            quadToRelative(12f, -12f, 28f, -11.5f)
            reflectiveQuadToRelative(28f, 12.5f)
            quadToRelative(11f, 12f, 11.5f, 28f)
            reflectiveQuadTo(308f, 485f)
            lineToRelative(-75f, 75f)
            horizontalLineToRelative(247f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(520f, 600f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 640f)
            lineTo(233f, 640f)
            close()
            moveTo(727f, 400f)
            lineTo(480f, 400f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 360f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(480f, 320f)
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
            lineTo(708f, 532f)
            quadToRelative(-12f, 12f, -28f, 11.5f)
            reflectiveQuadTo(652f, 531f)
            quadToRelative(-11f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(11.5f, -28f)
            lineToRelative(75f, -75f)
            close()
        }
    }.build()
}
