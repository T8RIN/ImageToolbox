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

val Icons.Rounded.Language: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Language",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 880f)
            quadToRelative(-82f, 0f, -155f, -31.5f)
            reflectiveQuadToRelative(-127.5f, -86f)
            quadTo(143f, 708f, 111.5f, 635f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -155.5f)
            reflectiveQuadToRelative(86f, -127f)
            quadTo(252f, 143f, 325f, 111.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(83f, 0f, 155.5f, 31.5f)
            reflectiveQuadToRelative(127f, 86f)
            quadToRelative(54.5f, 54.5f, 86f, 127f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 82f, -31.5f, 155f)
            reflectiveQuadToRelative(-86f, 127.5f)
            quadToRelative(-54.5f, 54.5f, -127f, 86f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(480f, 798f)
            quadToRelative(26f, -36f, 45f, -75f)
            reflectiveQuadToRelative(31f, -83f)
            lineTo(404f, 640f)
            quadToRelative(12f, 44f, 31f, 83f)
            reflectiveQuadToRelative(45f, 75f)
            close()
            moveTo(376f, 782f)
            quadToRelative(-18f, -33f, -31.5f, -68.5f)
            reflectiveQuadTo(322f, 640f)
            lineTo(204f, 640f)
            quadToRelative(29f, 50f, 72.5f, 87f)
            reflectiveQuadToRelative(99.5f, 55f)
            close()
            moveTo(584f, 782f)
            quadToRelative(56f, -18f, 99.5f, -55f)
            reflectiveQuadToRelative(72.5f, -87f)
            lineTo(638f, 640f)
            quadToRelative(-9f, 38f, -22.5f, 73.5f)
            reflectiveQuadTo(584f, 782f)
            close()
            moveTo(170f, 560f)
            horizontalLineToRelative(136f)
            quadToRelative(-3f, -20f, -4.5f, -39.5f)
            reflectiveQuadTo(300f, 480f)
            quadToRelative(0f, -21f, 1.5f, -40.5f)
            reflectiveQuadTo(306f, 400f)
            lineTo(170f, 400f)
            quadToRelative(-5f, 20f, -7.5f, 39.5f)
            reflectiveQuadTo(160f, 480f)
            quadToRelative(0f, 21f, 2.5f, 40.5f)
            reflectiveQuadTo(170f, 560f)
            close()
            moveTo(386f, 560f)
            horizontalLineToRelative(188f)
            quadToRelative(3f, -20f, 4.5f, -39.5f)
            reflectiveQuadTo(580f, 480f)
            quadToRelative(0f, -21f, -1.5f, -40.5f)
            reflectiveQuadTo(574f, 400f)
            lineTo(386f, 400f)
            quadToRelative(-3f, 20f, -4.5f, 39.5f)
            reflectiveQuadTo(380f, 480f)
            quadToRelative(0f, 21f, 1.5f, 40.5f)
            reflectiveQuadTo(386f, 560f)
            close()
            moveTo(654f, 560f)
            horizontalLineToRelative(136f)
            quadToRelative(5f, -20f, 7.5f, -39.5f)
            reflectiveQuadTo(800f, 480f)
            quadToRelative(0f, -21f, -2.5f, -40.5f)
            reflectiveQuadTo(790f, 400f)
            lineTo(654f, 400f)
            quadToRelative(3f, 20f, 4.5f, 39.5f)
            reflectiveQuadTo(660f, 480f)
            quadToRelative(0f, 21f, -1.5f, 40.5f)
            reflectiveQuadTo(654f, 560f)
            close()
            moveTo(638f, 320f)
            horizontalLineToRelative(118f)
            quadToRelative(-29f, -50f, -72.5f, -87f)
            reflectiveQuadTo(584f, 178f)
            quadToRelative(18f, 33f, 31.5f, 68.5f)
            reflectiveQuadTo(638f, 320f)
            close()
            moveTo(404f, 320f)
            horizontalLineToRelative(152f)
            quadToRelative(-12f, -44f, -31f, -83f)
            reflectiveQuadToRelative(-45f, -75f)
            quadToRelative(-26f, 36f, -45f, 75f)
            reflectiveQuadToRelative(-31f, 83f)
            close()
            moveTo(204f, 320f)
            horizontalLineToRelative(118f)
            quadToRelative(9f, -38f, 22.5f, -73.5f)
            reflectiveQuadTo(376f, 178f)
            quadToRelative(-56f, 18f, -99.5f, 55f)
            reflectiveQuadTo(204f, 320f)
            close()
        }
    }.build()
}
