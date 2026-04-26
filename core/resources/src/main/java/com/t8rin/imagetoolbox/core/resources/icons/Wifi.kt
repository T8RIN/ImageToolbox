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

val Icons.Rounded.Wifi: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Wifi",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 840f)
            quadToRelative(-42f, 0f, -71f, -29f)
            reflectiveQuadToRelative(-29f, -71f)
            quadToRelative(0f, -42f, 29f, -71f)
            reflectiveQuadToRelative(71f, -29f)
            quadToRelative(42f, 0f, 71f, 29f)
            reflectiveQuadToRelative(29f, 71f)
            quadToRelative(0f, 42f, -29f, 71f)
            reflectiveQuadToRelative(-71f, 29f)
            close()
            moveTo(480f, 400f)
            quadToRelative(75f, 0f, 142.5f, 24f)
            reflectiveQuadTo(745f, 490f)
            quadToRelative(20f, 15f, 20.5f, 39.5f)
            reflectiveQuadTo(748f, 572f)
            quadToRelative(-17f, 17f, -42f, 17.5f)
            reflectiveQuadTo(661f, 576f)
            quadToRelative(-38f, -26f, -84f, -41f)
            reflectiveQuadToRelative(-97f, -15f)
            quadToRelative(-51f, 0f, -97f, 15f)
            reflectiveQuadToRelative(-84f, 41f)
            quadToRelative(-20f, 14f, -45f, 13f)
            reflectiveQuadToRelative(-42f, -18f)
            quadToRelative(-17f, -18f, -17f, -42.5f)
            reflectiveQuadToRelative(20f, -39.5f)
            quadToRelative(55f, -42f, 122.5f, -65.5f)
            reflectiveQuadTo(480f, 400f)
            close()
            moveTo(480f, 160f)
            quadToRelative(125f, 0f, 235.5f, 41f)
            reflectiveQuadTo(914f, 317f)
            quadToRelative(20f, 17f, 21f, 42f)
            reflectiveQuadToRelative(-17f, 43f)
            quadToRelative(-17f, 17f, -42f, 17.5f)
            reflectiveQuadTo(831f, 404f)
            quadToRelative(-72f, -59f, -161.5f, -91.5f)
            reflectiveQuadTo(480f, 280f)
            quadToRelative(-100f, 0f, -189.5f, 32.5f)
            reflectiveQuadTo(129f, 404f)
            quadToRelative(-20f, 16f, -45f, 15.5f)
            reflectiveQuadTo(42f, 402f)
            quadToRelative(-18f, -18f, -17f, -43f)
            reflectiveQuadToRelative(21f, -42f)
            quadToRelative(88f, -75f, 198.5f, -116f)
            reflectiveQuadTo(480f, 160f)
            close()
        }
    }.build()
}
