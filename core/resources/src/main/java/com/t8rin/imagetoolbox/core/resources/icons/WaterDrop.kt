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

val Icons.Outlined.WaterDrop: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.WaterDrop",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 880f)
            quadToRelative(-137f, 0f, -228.5f, -94f)
            reflectiveQuadTo(160f, 552f)
            quadToRelative(0f, -62f, 28f, -124f)
            reflectiveQuadToRelative(70f, -119f)
            quadToRelative(42f, -57f, 91f, -107f)
            reflectiveQuadToRelative(91f, -87f)
            quadToRelative(8f, -8f, 18.5f, -11.5f)
            reflectiveQuadTo(480f, 100f)
            quadToRelative(11f, 0f, 21.5f, 3.5f)
            reflectiveQuadTo(520f, 115f)
            quadToRelative(42f, 37f, 91f, 87f)
            reflectiveQuadToRelative(91f, 107f)
            quadToRelative(42f, 57f, 70f, 119f)
            reflectiveQuadToRelative(28f, 124f)
            quadToRelative(0f, 140f, -91.5f, 234f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(480f, 800f)
            quadToRelative(104f, 0f, 172f, -70.5f)
            reflectiveQuadTo(720f, 552f)
            quadToRelative(0f, -73f, -60.5f, -165f)
            reflectiveQuadTo(480f, 186f)
            quadTo(361f, 295f, 300.5f, 387f)
            reflectiveQuadTo(240f, 552f)
            quadToRelative(0f, 107f, 68f, 177.5f)
            reflectiveQuadTo(480f, 800f)
            close()
            moveTo(480f, 480f)
            close()
            moveTo(491f, 760f)
            quadToRelative(12f, -1f, 20.5f, -9.5f)
            reflectiveQuadTo(520f, 730f)
            quadToRelative(0f, -14f, -9f, -22.5f)
            reflectiveQuadToRelative(-23f, -7.5f)
            quadToRelative(-41f, 3f, -87f, -22.5f)
            reflectiveQuadTo(343f, 585f)
            quadToRelative(-2f, -11f, -10.5f, -18f)
            reflectiveQuadToRelative(-19.5f, -7f)
            quadToRelative(-14f, 0f, -23f, 10.5f)
            reflectiveQuadToRelative(-6f, 24.5f)
            quadToRelative(17f, 91f, 80f, 130f)
            reflectiveQuadToRelative(127f, 35f)
            close()
        }
    }.build()
}
