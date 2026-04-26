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

val Icons.Rounded.MotionPhotosAuto: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MotionPhotosAuto",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(416f, 538f)
            horizontalLineToRelative(128f)
            lineToRelative(24f, 65f)
            quadToRelative(3f, 8f, 9.5f, 12.5f)
            reflectiveQuadTo(592f, 620f)
            quadToRelative(14f, 0f, 21.5f, -11f)
            reflectiveQuadToRelative(2.5f, -24f)
            lineTo(514f, 317f)
            quadToRelative(-3f, -8f, -9.5f, -12.5f)
            reflectiveQuadTo(490f, 300f)
            horizontalLineToRelative(-20f)
            quadToRelative(-8f, 0f, -14.5f, 4.5f)
            reflectiveQuadTo(446f, 317f)
            lineTo(345f, 586f)
            quadToRelative(-5f, 12f, 2f, 23f)
            reflectiveQuadToRelative(21f, 11f)
            quadToRelative(8f, 0f, 14.5f, -4.5f)
            reflectiveQuadTo(392f, 603f)
            lineToRelative(24f, -65f)
            close()
            moveTo(432f, 492f)
            lineTo(478f, 360f)
            horizontalLineToRelative(4f)
            lineToRelative(46f, 132f)
            horizontalLineToRelative(-96f)
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
            moveTo(220f, 280f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(160f, 220f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(220f, 160f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(280f, 220f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(220f, 280f)
            close()
            moveTo(240f, 480f)
            quadToRelative(0f, -100f, 70f, -170f)
            reflectiveQuadToRelative(170f, -70f)
            quadToRelative(100f, 0f, 170f, 70f)
            reflectiveQuadToRelative(70f, 170f)
            quadToRelative(0f, 100f, -70f, 170f)
            reflectiveQuadToRelative(-170f, 70f)
            quadToRelative(-100f, 0f, -170f, -70f)
            reflectiveQuadToRelative(-70f, -170f)
            close()
        }
    }.build()
}
