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

val Icons.Rounded.SettingsEthernet: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.SettingsEthernet",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(806f, 480f)
            lineTo(652f, 324f)
            quadToRelative(-11f, -11f, -11.5f, -27.5f)
            reflectiveQuadTo(652f, 268f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(184f, 184f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineTo(708f, 692f)
            quadToRelative(-11f, 11f, -27.5f, 11.5f)
            reflectiveQuadTo(652f, 692f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(154f, -156f)
            close()
            moveTo(154f, 480f)
            lineTo(308f, 636f)
            quadToRelative(11f, 11f, 11.5f, 27.5f)
            reflectiveQuadTo(308f, 692f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(68f, 508f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadTo(57f, 480f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(184f, -184f)
            quadToRelative(11f, -11f, 27.5f, -11.5f)
            reflectiveQuadTo(308f, 268f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineTo(154f, 480f)
            close()
            moveTo(320f, 520f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(280f, 480f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(320f, 440f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(360f, 480f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(320f, 520f)
            close()
            moveTo(480f, 520f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 480f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(480f, 440f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(520f, 480f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 520f)
            close()
            moveTo(640f, 520f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 480f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 440f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 480f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 520f)
            close()
        }
    }.build()
}
