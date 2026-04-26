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

val Icons.Rounded.RotateRight: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.RotateRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(790f, 482f)
            quadToRelative(-12f, 0f, -23.5f, -8.5f)
            reflectiveQuadTo(752f, 452f)
            quadToRelative(-5f, -19f, -12.5f, -37.5f)
            reflectiveQuadTo(722f, 379f)
            quadToRelative(-7f, -11f, -5f, -24.5f)
            reflectiveQuadToRelative(11f, -22.5f)
            quadToRelative(15f, -14f, 34.5f, -12f)
            reflectiveQuadToRelative(29.5f, 19f)
            quadToRelative(13f, 22f, 23f, 45f)
            reflectiveQuadToRelative(16f, 48f)
            quadToRelative(5f, 20f, -7.5f, 35f)
            reflectiveQuadTo(790f, 482f)
            close()
            moveTo(522f, 830f)
            quadToRelative(0f, -12f, 8.5f, -23.5f)
            reflectiveQuadTo(552f, 792f)
            quadToRelative(19f, -5f, 37.5f, -12.5f)
            reflectiveQuadTo(625f, 762f)
            quadToRelative(11f, -7f, 24.5f, -5f)
            reflectiveQuadToRelative(22.5f, 11f)
            quadToRelative(14f, 15f, 12f, 34.5f)
            reflectiveQuadTo(665f, 832f)
            quadToRelative(-23f, 13f, -46f, 22.5f)
            reflectiveQuadTo(572f, 871f)
            quadToRelative(-20f, 5f, -35f, -7f)
            reflectiveQuadToRelative(-15f, -34f)
            close()
            moveTo(728f, 712f)
            quadToRelative(-9f, -8f, -11f, -22f)
            reflectiveQuadToRelative(5f, -25f)
            quadToRelative(10f, -17f, 17.5f, -35.5f)
            reflectiveQuadTo(752f, 592f)
            quadToRelative(3f, -13f, 14f, -21.5f)
            reflectiveQuadToRelative(24f, -8.5f)
            quadToRelative(22f, 0f, 34f, 15f)
            reflectiveQuadToRelative(7f, 35f)
            quadToRelative(-7f, 24f, -16.5f, 47f)
            reflectiveQuadTo(792f, 705f)
            quadToRelative(-10f, 17f, -29.5f, 19f)
            reflectiveQuadTo(728f, 712f)
            close()
            moveTo(393f, 870f)
            quadToRelative(-119f, -32f, -195f, -128f)
            reflectiveQuadToRelative(-76f, -220f)
            quadToRelative(0f, -150f, 105f, -255f)
            reflectiveQuadToRelative(255f, -105f)
            horizontalLineToRelative(6f)
            lineToRelative(-35f, -35f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(12f, -29f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(29f, 11f)
            lineToRelative(103f, 104f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineTo(510f, 334f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(36f, -36f)
            horizontalLineToRelative(-8f)
            quadToRelative(-117f, 0f, -198.5f, 81.5f)
            reflectiveQuadTo(202f, 522f)
            quadToRelative(0f, 97f, 59f, 171.5f)
            reflectiveQuadTo(412f, 792f)
            quadToRelative(13f, 3f, 21.5f, 14f)
            reflectiveQuadToRelative(8.5f, 24f)
            quadToRelative(0f, 21f, -14.5f, 33f)
            reflectiveQuadTo(393f, 870f)
            close()
        }
    }.build()
}
