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

val Icons.Rounded.MobileCast: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MobileCast",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(211.5f, 908.5f)
            quadTo(200f, 897f, 200f, 880f)
            reflectiveQuadToRelative(11.5f, -28.5f)
            quadTo(223f, 840f, 240f, 840f)
            reflectiveQuadToRelative(28.5f, 11.5f)
            quadTo(280f, 863f, 280f, 880f)
            reflectiveQuadToRelative(-11.5f, 28.5f)
            quadTo(257f, 920f, 240f, 920f)
            reflectiveQuadToRelative(-28.5f, -11.5f)
            close()
            moveTo(313f, 807f)
            quadToRelative(-15f, -15f, -33.5f, -26f)
            reflectiveQuadTo(240f, 765f)
            quadToRelative(-17f, -5f, -28.5f, -16.5f)
            reflectiveQuadTo(200f, 720f)
            quadToRelative(0f, -17f, 12f, -28.5f)
            reflectiveQuadToRelative(28f, -8.5f)
            quadToRelative(37f, 6f, 70.5f, 23.5f)
            reflectiveQuadTo(370f, 750f)
            quadToRelative(26f, 26f, 43.5f, 59.5f)
            reflectiveQuadTo(437f, 880f)
            quadToRelative(3f, 16f, -8.5f, 28f)
            reflectiveQuadTo(400f, 920f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(355f, 880f)
            quadToRelative(-5f, -21f, -16f, -39.5f)
            reflectiveQuadTo(313f, 807f)
            close()
            moveTo(340.5f, 632.5f)
            quadTo(293f, 609f, 240f, 602f)
            quadToRelative(-17f, -2f, -28.5f, -13.5f)
            reflectiveQuadTo(200f, 560f)
            quadToRelative(0f, -17f, 12f, -28.5f)
            reflectiveQuadToRelative(29f, -9.5f)
            quadToRelative(69f, 7f, 131f, 36.5f)
            reflectiveQuadTo(483f, 637f)
            quadToRelative(49f, 49f, 77.5f, 111f)
            reflectiveQuadTo(597f, 879f)
            quadToRelative(2f, 17f, -9f, 29f)
            reflectiveQuadToRelative(-28f, 12f)
            quadToRelative(-17f, 0f, -29f, -11.5f)
            reflectiveQuadTo(517f, 880f)
            quadToRelative(-7f, -53f, -30f, -100.5f)
            reflectiveQuadTo(426f, 694f)
            quadToRelative(-38f, -38f, -85.5f, -61.5f)
            close()
            moveTo(508.5f, 228.5f)
            quadTo(520f, 217f, 520f, 200f)
            reflectiveQuadToRelative(-11.5f, -28.5f)
            quadTo(497f, 160f, 480f, 160f)
            reflectiveQuadToRelative(-28.5f, 11.5f)
            quadTo(440f, 183f, 440f, 200f)
            reflectiveQuadToRelative(11.5f, 28.5f)
            quadTo(463f, 240f, 480f, 240f)
            reflectiveQuadToRelative(28.5f, -11.5f)
            close()
            moveTo(760f, 436f)
            verticalLineToRelative(443f)
            quadToRelative(0f, 18f, -11.5f, 31.5f)
            reflectiveQuadTo(720f, 924f)
            quadToRelative(-17f, 0f, -28f, -11f)
            reflectiveQuadToRelative(-13f, -28f)
            quadToRelative(-13f, -180f, -139f, -305.5f)
            reflectiveQuadTo(234f, 441f)
            quadToRelative(-14f, -1f, -24f, -10.5f)
            reflectiveQuadTo(200f, 407f)
            verticalLineToRelative(-287f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 40f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 120f)
            verticalLineToRelative(124f)
            quadToRelative(18f, 7f, 29f, 22f)
            reflectiveQuadToRelative(11f, 34f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 19f, -11f, 34f)
            reflectiveQuadToRelative(-29f, 22f)
            close()
        }
    }.build()
}
