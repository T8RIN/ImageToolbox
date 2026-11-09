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

val Icons.Outlined.DonutLarge: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DonutLarge",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(252.5f, 646.5f)
            quadTo(305f, 720f, 391f, 747f)
            quadToRelative(18f, 6f, 29.5f, 22.5f)
            reflectiveQuadTo(432f, 806f)
            quadToRelative(0f, 29f, -24.5f, 46f)
            reflectiveQuadToRelative(-53.5f, 7f)
            quadToRelative(-124f, -41f, -199f, -145f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -130f, 75f, -234f)
            reflectiveQuadToRelative(199f, -145f)
            quadToRelative(29f, -10f, 53.5f, 7f)
            reflectiveQuadToRelative(24.5f, 46f)
            quadToRelative(0f, 20f, -11.5f, 36.5f)
            reflectiveQuadTo(391f, 213f)
            quadToRelative(-86f, 27f, -138.5f, 100.5f)
            reflectiveQuadTo(200f, 480f)
            quadToRelative(0f, 93f, 52.5f, 166.5f)
            close()
            moveTo(762f, 762f)
            quadToRelative(-32f, 33f, -72.5f, 57.5f)
            reflectiveQuadTo(605f, 859f)
            quadToRelative(-29f, 10f, -53.5f, -7f)
            reflectiveQuadTo(527f, 806f)
            quadToRelative(0f, -20f, 11.5f, -36.5f)
            reflectiveQuadTo(568f, 747f)
            quadToRelative(31f, -10f, 59.5f, -27.5f)
            reflectiveQuadTo(679f, 679f)
            quadToRelative(23f, -24f, 40f, -51.5f)
            reflectiveQuadToRelative(28f, -59.5f)
            quadToRelative(6f, -18f, 22.5f, -29.5f)
            reflectiveQuadTo(806f, 527f)
            quadToRelative(29f, 0f, 46f, 24.5f)
            reflectiveQuadToRelative(7f, 53.5f)
            quadToRelative(-15f, 44f, -39.5f, 84f)
            reflectiveQuadTo(762f, 762f)
            close()
            moveTo(762f, 198f)
            quadToRelative(33f, 33f, 57.5f, 73f)
            reflectiveQuadToRelative(39.5f, 84f)
            quadToRelative(10f, 29f, -7f, 53.5f)
            reflectiveQuadTo(806f, 433f)
            quadToRelative(-20f, 0f, -36.5f, -11.5f)
            reflectiveQuadTo(747f, 392f)
            quadToRelative(-11f, -32f, -28f, -59.5f)
            reflectiveQuadTo(679f, 281f)
            quadToRelative(-23f, -23f, -51.5f, -40.5f)
            reflectiveQuadTo(568f, 213f)
            quadToRelative(-18f, -6f, -29.5f, -22.5f)
            reflectiveQuadTo(527f, 154f)
            quadToRelative(0f, -29f, 24.5f, -46f)
            reflectiveQuadToRelative(53.5f, -7f)
            quadToRelative(44f, 15f, 84.5f, 39.5f)
            reflectiveQuadTo(762f, 198f)
            close()
        }
    }.build()
}
