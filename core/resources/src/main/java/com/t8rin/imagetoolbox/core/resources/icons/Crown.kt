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

val Icons.Rounded.Crown: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Crown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(240f, 800f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(200f, 760f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(240f, 720f)
            horizontalLineToRelative(480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(760f, 760f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(720f, 800f)
            lineTo(240f, 800f)
            close()
            moveTo(268f, 660f)
            quadToRelative(-29f, 0f, -51.5f, -19f)
            reflectiveQuadTo(189f, 593f)
            lineToRelative(-40f, -254f)
            quadToRelative(-2f, 0f, -4.5f, 0.5f)
            reflectiveQuadToRelative(-4.5f, 0.5f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(80f, 280f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(140f, 220f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(200f, 280f)
            quadToRelative(0f, 7f, -1.5f, 13f)
            reflectiveQuadToRelative(-3.5f, 11f)
            lineToRelative(125f, 56f)
            lineToRelative(125f, -171f)
            quadToRelative(-11f, -8f, -18f, -21f)
            reflectiveQuadToRelative(-7f, -28f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(540f, 140f)
            quadToRelative(0f, 15f, -7f, 28f)
            reflectiveQuadToRelative(-18f, 21f)
            lineToRelative(125f, 171f)
            lineToRelative(125f, -56f)
            quadToRelative(-2f, -5f, -3.5f, -11f)
            reflectiveQuadToRelative(-1.5f, -13f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(820f, 220f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(880f, 280f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(820f, 340f)
            quadToRelative(-2f, 0f, -4.5f, -0.5f)
            reflectiveQuadToRelative(-4.5f, -0.5f)
            lineToRelative(-40f, 254f)
            quadToRelative(-5f, 29f, -27.5f, 48f)
            reflectiveQuadTo(692f, 660f)
            lineTo(268f, 660f)
            close()
        }
    }.build()
}
