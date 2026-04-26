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

val Icons.Rounded.LocationOn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.LocationOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 853f)
            quadToRelative(-14f, 0f, -28f, -5f)
            reflectiveQuadToRelative(-25f, -15f)
            quadToRelative(-65f, -60f, -115f, -117f)
            reflectiveQuadToRelative(-83.5f, -110.5f)
            quadToRelative(-33.5f, -53.5f, -51f, -103f)
            reflectiveQuadTo(160f, 408f)
            quadToRelative(0f, -150f, 96.5f, -239f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(127f, 0f, 223.5f, 89f)
            reflectiveQuadTo(800f, 408f)
            quadToRelative(0f, 45f, -17.5f, 94.5f)
            reflectiveQuadToRelative(-51f, 103f)
            quadTo(698f, 659f, 648f, 716f)
            reflectiveQuadTo(533f, 833f)
            quadToRelative(-11f, 10f, -25f, 15f)
            reflectiveQuadToRelative(-28f, 5f)
            close()
            moveTo(480f, 480f)
            quadToRelative(33f, 0f, 56.5f, -23.5f)
            reflectiveQuadTo(560f, 400f)
            quadToRelative(0f, -33f, -23.5f, -56.5f)
            reflectiveQuadTo(480f, 320f)
            quadToRelative(-33f, 0f, -56.5f, 23.5f)
            reflectiveQuadTo(400f, 400f)
            quadToRelative(0f, 33f, 23.5f, 56.5f)
            reflectiveQuadTo(480f, 480f)
            close()
        }
    }.build()
}
