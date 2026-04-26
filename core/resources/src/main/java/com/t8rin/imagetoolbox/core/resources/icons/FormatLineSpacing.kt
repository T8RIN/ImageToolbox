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

val Icons.Rounded.FormatLineSpacing: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FormatLineSpacing",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(200f, 314f)
            lineToRelative(-36f, 35f)
            quadToRelative(-11f, 11f, -27.5f, 11f)
            reflectiveQuadTo(108f, 348f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(104f, -104f)
            quadToRelative(6f, -6f, 13f, -8.5f)
            reflectiveQuadToRelative(15f, -2.5f)
            quadToRelative(8f, 0f, 15f, 2.5f)
            reflectiveQuadToRelative(13f, 8.5f)
            lineToRelative(104f, 104f)
            quadToRelative(11f, 11f, 11.5f, 27.5f)
            reflectiveQuadTo(372f, 348f)
            quadToRelative(-11f, 11f, -27.5f, 11.5f)
            reflectiveQuadTo(316f, 349f)
            lineToRelative(-36f, -35f)
            verticalLineToRelative(332f)
            lineToRelative(36f, -35f)
            quadToRelative(11f, -11f, 27.5f, -11f)
            reflectiveQuadToRelative(28.5f, 12f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineTo(268f, 772f)
            quadToRelative(-6f, 6f, -13f, 8.5f)
            reflectiveQuadToRelative(-15f, 2.5f)
            quadToRelative(-8f, 0f, -15f, -2.5f)
            reflectiveQuadToRelative(-13f, -8.5f)
            lineTo(108f, 668f)
            quadToRelative(-11f, -11f, -11.5f, -27.5f)
            reflectiveQuadTo(108f, 612f)
            quadToRelative(11f, -11f, 27.5f, -11.5f)
            reflectiveQuadTo(164f, 611f)
            lineToRelative(36f, 35f)
            verticalLineToRelative(-332f)
            close()
            moveTo(520f, 760f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 720f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 680f)
            horizontalLineToRelative(320f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 720f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 760f)
            lineTo(520f, 760f)
            close()
            moveTo(520f, 520f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 480f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 440f)
            horizontalLineToRelative(320f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 520f)
            lineTo(520f, 520f)
            close()
            moveTo(520f, 280f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 240f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 200f)
            horizontalLineToRelative(320f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 280f)
            lineTo(520f, 280f)
            close()
        }
    }.build()
}
