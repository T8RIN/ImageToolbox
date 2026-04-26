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

val Icons.Rounded.AlternateEmail: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.AlternateEmail",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(83f, 0f, 156f, 31.5f)
            reflectiveQuadTo(763f, 197f)
            quadToRelative(54f, 54f, 85.5f, 127f)
            reflectiveQuadTo(880f, 480f)
            verticalLineToRelative(58f)
            quadToRelative(0f, 59f, -40.5f, 100.5f)
            reflectiveQuadTo(740f, 680f)
            quadToRelative(-35f, 0f, -66f, -15f)
            reflectiveQuadToRelative(-52f, -43f)
            quadToRelative(-29f, 29f, -65.5f, 43.5f)
            reflectiveQuadTo(480f, 680f)
            quadToRelative(-83f, 0f, -141.5f, -58.5f)
            reflectiveQuadTo(280f, 480f)
            quadToRelative(0f, -83f, 58.5f, -141.5f)
            reflectiveQuadTo(480f, 280f)
            quadToRelative(83f, 0f, 141.5f, 58.5f)
            reflectiveQuadTo(680f, 480f)
            verticalLineToRelative(58f)
            quadToRelative(0f, 26f, 17f, 44f)
            reflectiveQuadToRelative(43f, 18f)
            quadToRelative(26f, 0f, 43f, -18f)
            reflectiveQuadToRelative(17f, -44f)
            verticalLineToRelative(-58f)
            quadToRelative(0f, -134f, -93f, -227f)
            reflectiveQuadToRelative(-227f, -93f)
            quadToRelative(-134f, 0f, -227f, 93f)
            reflectiveQuadToRelative(-93f, 227f)
            quadToRelative(0f, 134f, 93f, 227f)
            reflectiveQuadToRelative(227f, 93f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 840f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 880f)
            lineTo(480f, 880f)
            close()
            moveTo(480f, 600f)
            quadToRelative(50f, 0f, 85f, -35f)
            reflectiveQuadToRelative(35f, -85f)
            quadToRelative(0f, -50f, -35f, -85f)
            reflectiveQuadToRelative(-85f, -35f)
            quadToRelative(-50f, 0f, -85f, 35f)
            reflectiveQuadToRelative(-35f, 85f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            close()
        }
    }.build()
}
