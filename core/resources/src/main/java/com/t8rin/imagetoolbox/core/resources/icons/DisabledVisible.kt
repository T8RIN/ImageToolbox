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

val Icons.Rounded.DisabledVisible: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.DisabledVisible",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(680f, 800f)
            quadToRelative(59f, 0f, 109.5f, -27f)
            reflectiveQuadToRelative(80.5f, -73f)
            quadToRelative(-30f, -46f, -80.5f, -73f)
            reflectiveQuadTo(680f, 600f)
            quadToRelative(-59f, 0f, -109.5f, 27f)
            reflectiveQuadTo(490f, 700f)
            quadToRelative(30f, 46f, 80.5f, 73f)
            reflectiveQuadTo(680f, 800f)
            close()
            moveTo(680f, 880f)
            quadToRelative(-78f, 0f, -143f, -34f)
            reflectiveQuadToRelative(-106f, -91f)
            quadToRelative(-8f, -12f, -12.5f, -25.5f)
            reflectiveQuadTo(414f, 702f)
            quadToRelative(0f, -14f, 4f, -29f)
            reflectiveQuadToRelative(13f, -28f)
            quadToRelative(40f, -57f, 105.5f, -91f)
            reflectiveQuadTo(680f, 520f)
            quadToRelative(78f, 0f, 143f, 34f)
            reflectiveQuadToRelative(106f, 91f)
            quadToRelative(8f, 12f, 12.5f, 25.5f)
            reflectiveQuadTo(946f, 698f)
            quadToRelative(0f, 14f, -4f, 29f)
            reflectiveQuadToRelative(-13f, 28f)
            quadToRelative(-40f, 57f, -105.5f, 91f)
            reflectiveQuadTo(680f, 880f)
            close()
            moveTo(680f, 760f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(620f, 700f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(680f, 640f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(740f, 700f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(680f, 760f)
            close()
            moveTo(319f, 847f)
            quadToRelative(-110f, -48f, -174.5f, -147f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(155f, 0f, 267.5f, 101f)
            reflectiveQuadTo(877f, 435f)
            quadToRelative(2f, 20f, -9f, 30.5f)
            reflectiveQuadTo(842f, 478f)
            quadToRelative(-15f, 2f, -28.5f, -6.5f)
            reflectiveQuadTo(798f, 443f)
            quadToRelative(-14f, -121f, -105f, -202f)
            reflectiveQuadToRelative(-213f, -81f)
            quadToRelative(-56f, 0f, -105.5f, 18f)
            reflectiveQuadTo(284f, 228f)
            lineToRelative(209f, 209f)
            quadToRelative(14f, 14f, 12.5f, 29.5f)
            reflectiveQuadTo(493f, 493f)
            quadToRelative(-11f, 11f, -26.5f, 12.5f)
            reflectiveQuadTo(437f, 493f)
            lineTo(228f, 284f)
            quadToRelative(-32f, 41f, -50f, 90.5f)
            reflectiveQuadTo(160f, 480f)
            quadToRelative(0f, 99f, 53f, 177.5f)
            reflectiveQuadTo(351f, 773f)
            quadToRelative(20f, 8f, 24f, 23.5f)
            reflectiveQuadToRelative(-2f, 29.5f)
            quadToRelative(-6f, 14f, -20.5f, 21.5f)
            reflectiveQuadTo(319f, 847f)
            close()
        }
    }.build()
}
