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

val Icons.Rounded.RssFeed: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.RssFeed",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 680f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(280f, 760f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(200f, 840f)
            close()
            moveTo(740f, 840f)
            quadToRelative(-26f, 0f, -43.5f, -19f)
            reflectiveQuadTo(676f, 776f)
            quadToRelative(-11f, -97f, -52.5f, -181.5f)
            reflectiveQuadTo(516f, 444f)
            quadToRelative(-66f, -66f, -150.5f, -107.5f)
            reflectiveQuadTo(184f, 284f)
            quadToRelative(-26f, -3f, -45f, -20.5f)
            reflectiveQuadTo(120f, 220f)
            quadToRelative(0f, -26f, 18f, -42.5f)
            reflectiveQuadToRelative(43f, -14.5f)
            quadToRelative(123f, 11f, 230.5f, 62.5f)
            reflectiveQuadTo(601f, 359f)
            quadToRelative(82f, 82f, 133.5f, 189.5f)
            reflectiveQuadTo(797f, 779f)
            quadToRelative(2f, 25f, -14.5f, 43f)
            reflectiveQuadTo(740f, 840f)
            close()
            moveTo(500f, 840f)
            quadToRelative(-25f, 0f, -43f, -17.5f)
            reflectiveQuadTo(434f, 780f)
            quadToRelative(-9f, -49f, -31.5f, -90.5f)
            reflectiveQuadTo(346f, 614f)
            quadToRelative(-34f, -34f, -75.5f, -56.5f)
            reflectiveQuadTo(180f, 526f)
            quadToRelative(-25f, -5f, -42.5f, -23f)
            reflectiveQuadTo(120f, 460f)
            quadToRelative(0f, -26f, 18f, -43f)
            reflectiveQuadToRelative(43f, -13f)
            quadToRelative(73f, 10f, 136.5f, 42.5f)
            reflectiveQuadTo(431f, 529f)
            quadToRelative(50f, 50f, 82.5f, 113.5f)
            reflectiveQuadTo(556f, 779f)
            quadToRelative(4f, 25f, -13f, 43f)
            reflectiveQuadToRelative(-43f, 18f)
            close()
        }
    }.build()
}
