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

val Icons.Outlined.Favorite: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Favorite",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(451.5f, 808f)
            quadToRelative(-14.5f, -5f, -25.5f, -16f)
            lineToRelative(-69f, -63f)
            quadToRelative(-106f, -97f, -191.5f, -192.5f)
            reflectiveQuadTo(80f, 326f)
            quadToRelative(0f, -94f, 63f, -157f)
            reflectiveQuadToRelative(157f, -63f)
            quadToRelative(53f, 0f, 100f, 22.5f)
            reflectiveQuadToRelative(80f, 61.5f)
            quadToRelative(33f, -39f, 80f, -61.5f)
            reflectiveQuadTo(660f, 106f)
            quadToRelative(94f, 0f, 157f, 63f)
            reflectiveQuadToRelative(63f, 157f)
            quadToRelative(0f, 115f, -85f, 211f)
            reflectiveQuadTo(602f, 730f)
            lineToRelative(-68f, 62f)
            quadToRelative(-11f, 11f, -25.5f, 16f)
            reflectiveQuadToRelative(-28.5f, 5f)
            quadToRelative(-14f, 0f, -28.5f, -5f)
            close()
            moveTo(442f, 270f)
            quadToRelative(-29f, -41f, -62f, -62.5f)
            reflectiveQuadTo(300f, 186f)
            quadToRelative(-60f, 0f, -100f, 40f)
            reflectiveQuadToRelative(-40f, 100f)
            quadToRelative(0f, 52f, 37f, 110.5f)
            reflectiveQuadTo(285.5f, 550f)
            quadToRelative(51.5f, 55f, 106f, 103f)
            reflectiveQuadToRelative(88.5f, 79f)
            quadToRelative(34f, -31f, 88.5f, -79f)
            reflectiveQuadToRelative(106f, -103f)
            quadTo(726f, 495f, 763f, 436.5f)
            reflectiveQuadTo(800f, 326f)
            quadToRelative(0f, -60f, -40f, -100f)
            reflectiveQuadToRelative(-100f, -40f)
            quadToRelative(-47f, 0f, -80f, 21.5f)
            reflectiveQuadTo(518f, 270f)
            quadToRelative(-7f, 10f, -17f, 15f)
            reflectiveQuadToRelative(-21f, 5f)
            quadToRelative(-11f, 0f, -21f, -5f)
            reflectiveQuadToRelative(-17f, -15f)
            close()
            moveTo(480f, 459f)
            close()
        }
    }.build()
}
