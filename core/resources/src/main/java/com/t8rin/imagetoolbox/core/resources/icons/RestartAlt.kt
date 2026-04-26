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

val Icons.Rounded.RestartAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.RestartAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(393f, 828f)
            quadToRelative(-103f, -29f, -168f, -113.5f)
            reflectiveQuadTo(160f, 520f)
            quadToRelative(0f, -57f, 19f, -108.5f)
            reflectiveQuadToRelative(54f, -94.5f)
            quadToRelative(11f, -12f, 27f, -12.5f)
            reflectiveQuadToRelative(29f, 12.5f)
            quadToRelative(11f, 11f, 11.5f, 27f)
            reflectiveQuadTo(290f, 374f)
            quadToRelative(-24f, 31f, -37f, 68f)
            reflectiveQuadToRelative(-13f, 78f)
            quadToRelative(0f, 81f, 47.5f, 144.5f)
            reflectiveQuadTo(410f, 751f)
            quadToRelative(13f, 4f, 21.5f, 15f)
            reflectiveQuadToRelative(8.5f, 24f)
            quadToRelative(0f, 20f, -14f, 31.5f)
            reflectiveQuadToRelative(-33f, 6.5f)
            close()
            moveTo(567f, 828f)
            quadToRelative(-19f, 5f, -33f, -7f)
            reflectiveQuadToRelative(-14f, -32f)
            quadToRelative(0f, -12f, 8.5f, -23f)
            reflectiveQuadToRelative(21.5f, -15f)
            quadToRelative(75f, -24f, 122.5f, -87f)
            reflectiveQuadTo(720f, 520f)
            quadToRelative(0f, -100f, -70f, -170f)
            reflectiveQuadToRelative(-170f, -70f)
            horizontalLineToRelative(-3f)
            lineToRelative(16f, 16f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineToRelative(-84f, -84f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadToRelative(-2.5f, -15f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(84f, -84f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineToRelative(-16f, 16f)
            horizontalLineToRelative(3f)
            quadToRelative(134f, 0f, 227f, 93f)
            reflectiveQuadToRelative(93f, 227f)
            quadToRelative(0f, 109f, -65f, 194f)
            reflectiveQuadTo(567f, 828f)
            close()
        }
    }.build()
}
