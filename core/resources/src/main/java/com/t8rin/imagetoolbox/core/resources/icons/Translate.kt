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

val Icons.Rounded.Translate: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Translate",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveToRelative(603f, 758f)
            lineToRelative(-34f, 97f)
            quadToRelative(-4f, 11f, -14f, 18f)
            reflectiveQuadToRelative(-22f, 7f)
            quadToRelative(-20f, 0f, -32.5f, -16.5f)
            reflectiveQuadTo(496f, 827f)
            lineToRelative(152f, -402f)
            quadToRelative(5f, -11f, 15f, -18f)
            reflectiveQuadToRelative(22f, -7f)
            horizontalLineToRelative(30f)
            quadToRelative(12f, 0f, 22f, 7f)
            reflectiveQuadToRelative(15f, 18f)
            lineToRelative(152f, 403f)
            quadToRelative(8f, 19f, -4f, 35.5f)
            reflectiveQuadTo(868f, 880f)
            quadToRelative(-13f, 0f, -22.5f, -7f)
            reflectiveQuadTo(831f, 854f)
            lineToRelative(-34f, -96f)
            lineTo(603f, 758f)
            close()
            moveTo(362f, 559f)
            lineTo(188f, 732f)
            quadToRelative(-11f, 11f, -27.5f, 11.5f)
            reflectiveQuadTo(132f, 732f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(174f, -174f)
            quadToRelative(-35f, -35f, -63.5f, -80f)
            reflectiveQuadTo(190f, 320f)
            horizontalLineToRelative(84f)
            quadToRelative(20f, 39f, 40f, 68f)
            reflectiveQuadToRelative(48f, 58f)
            quadToRelative(33f, -33f, 68.5f, -92.5f)
            reflectiveQuadTo(484f, 240f)
            lineTo(80f, 240f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(40f, 200f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(80f, 160f)
            horizontalLineToRelative(240f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(360f, 80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(400f, 120f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(240f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 200f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 240f)
            horizontalLineToRelative(-76f)
            quadToRelative(-21f, 72f, -63f, 148f)
            reflectiveQuadToRelative(-83f, 116f)
            lineToRelative(96f, 98f)
            lineToRelative(-30f, 82f)
            lineToRelative(-122f, -125f)
            close()
            moveTo(628f, 688f)
            horizontalLineToRelative(144f)
            lineToRelative(-72f, -204f)
            lineToRelative(-72f, 204f)
            close()
        }
    }.build()
}
