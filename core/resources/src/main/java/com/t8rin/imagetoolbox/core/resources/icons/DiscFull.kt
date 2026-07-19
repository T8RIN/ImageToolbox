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

val Icons.Rounded.DiscFull: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.DiscFull",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(607.5f, 607.5f)
            quadTo(660f, 555f, 660f, 480f)
            reflectiveQuadToRelative(-52.5f, -127.5f)
            quadTo(555f, 300f, 480f, 300f)
            reflectiveQuadToRelative(-127.5f, 52.5f)
            quadTo(300f, 405f, 300f, 480f)
            reflectiveQuadToRelative(52.5f, 127.5f)
            quadTo(405f, 660f, 480f, 660f)
            reflectiveQuadToRelative(127.5f, -52.5f)
            close()
            moveTo(451.5f, 508.5f)
            quadTo(440f, 497f, 440f, 480f)
            reflectiveQuadToRelative(11.5f, -28.5f)
            quadTo(463f, 440f, 480f, 440f)
            reflectiveQuadToRelative(28.5f, 11.5f)
            quadTo(520f, 463f, 520f, 480f)
            reflectiveQuadToRelative(-11.5f, 28.5f)
            quadTo(497f, 520f, 480f, 520f)
            reflectiveQuadToRelative(-28.5f, -11.5f)
            close()
            moveTo(811.5f, 708.5f)
            quadTo(800f, 697f, 800f, 680f)
            verticalLineToRelative(-240f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(840f, 400f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 440f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            close()
            moveTo(811.5f, 868.5f)
            quadTo(800f, 857f, 800f, 840f)
            reflectiveQuadToRelative(11.5f, -28.5f)
            quadTo(823f, 800f, 840f, 800f)
            reflectiveQuadToRelative(28.5f, 11.5f)
            quadTo(880f, 823f, 880f, 840f)
            reflectiveQuadToRelative(-11.5f, 28.5f)
            quadTo(857f, 880f, 840f, 880f)
            reflectiveQuadToRelative(-28.5f, -11.5f)
            close()
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(103f, 0f, 191f, 48.5f)
            reflectiveQuadTo(814f, 261f)
            quadToRelative(13f, 19f, 1f, 39f)
            reflectiveQuadToRelative(-36f, 20f)
            horizontalLineToRelative(-19f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(720f, 360f)
            verticalLineToRelative(411f)
            quadToRelative(0f, 14f, -6.5f, 25.5f)
            reflectiveQuadTo(696f, 816f)
            quadToRelative(-49f, 31f, -104f, 47.5f)
            reflectiveQuadTo(480f, 880f)
            close()
        }
    }.build()
}
