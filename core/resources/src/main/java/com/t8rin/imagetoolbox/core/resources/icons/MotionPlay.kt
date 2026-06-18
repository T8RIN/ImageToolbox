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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.MotionPlay: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.MotionPlay",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(431f, 619f)
            lineToRelative(184f, -122f)
            quadToRelative(9f, -6f, 9f, -17f)
            reflectiveQuadToRelative(-9f, -17f)
            lineTo(431f, 341f)
            quadToRelative(-10f, -7f, -20.5f, -1.5f)
            reflectiveQuadTo(400f, 357f)
            verticalLineToRelative(246f)
            quadToRelative(0f, 12f, 10.5f, 17.5f)
            reflectiveQuadTo(431f, 619f)
            close()
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -32f, 5f, -64f)
            reflectiveQuadToRelative(15f, -63f)
            quadToRelative(5f, -16f, 20.5f, -21.5f)
            reflectiveQuadTo(150f, 334f)
            quadToRelative(15f, 8f, 21.5f, 23.5f)
            reflectiveQuadTo(173f, 390f)
            quadToRelative(-6f, 22f, -9.5f, 44.5f)
            reflectiveQuadTo(160f, 480f)
            quadToRelative(0f, 134f, 93f, 227f)
            reflectiveQuadToRelative(227f, 93f)
            quadToRelative(134f, 0f, 227f, -93f)
            reflectiveQuadToRelative(93f, -227f)
            quadToRelative(0f, -134f, -93f, -227f)
            reflectiveQuadToRelative(-227f, -93f)
            quadToRelative(-24f, 0f, -47.5f, 3.5f)
            reflectiveQuadTo(386f, 174f)
            quadToRelative(-17f, 5f, -32f, -1f)
            reflectiveQuadToRelative(-22f, -21f)
            quadToRelative(-7f, -15f, -0.5f, -30.5f)
            reflectiveQuadTo(354f, 101f)
            quadToRelative(30f, -11f, 62f, -16f)
            reflectiveQuadToRelative(64f, -5f)
            quadToRelative(83f, 0f, 156f, 31.5f)
            reflectiveQuadTo(763f, 197f)
            quadToRelative(54f, 54f, 85.5f, 127f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 83f, -31.5f, 156f)
            reflectiveQuadTo(763f, 763f)
            quadToRelative(-54f, 54f, -127f, 85.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(177.5f, 262.5f)
            quadTo(160f, 245f, 160f, 220f)
            reflectiveQuadToRelative(17.5f, -42.5f)
            quadTo(195f, 160f, 220f, 160f)
            reflectiveQuadToRelative(42.5f, 17.5f)
            quadTo(280f, 195f, 280f, 220f)
            reflectiveQuadToRelative(-17.5f, 42.5f)
            quadTo(245f, 280f, 220f, 280f)
            reflectiveQuadToRelative(-42.5f, -17.5f)
            close()
            moveTo(480f, 480f)
            close()
        }
    }.build()
}
