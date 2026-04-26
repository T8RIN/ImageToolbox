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

val Icons.Rounded.Speed: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Speed",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(418f, 620f)
            quadTo(443f, 645f, 481f, 643.5f)
            quadTo(519f, 642f, 536f, 616f)
            lineTo(705f, 363f)
            quadTo(714f, 349f, 702.5f, 337.5f)
            quadTo(691f, 326f, 677f, 335f)
            lineTo(424f, 504f)
            quadTo(398f, 522f, 395.5f, 558.5f)
            quadTo(393f, 595f, 418f, 620f)
            close()
            moveTo(204f, 800f)
            quadTo(182f, 800f, 163.5f, 790.5f)
            quadTo(145f, 781f, 134f, 762f)
            quadTo(108f, 715f, 94f, 664.5f)
            quadTo(80f, 614f, 80f, 560f)
            quadTo(80f, 477f, 111.5f, 404f)
            quadTo(143f, 331f, 197f, 277f)
            quadTo(251f, 223f, 324f, 191.5f)
            quadTo(397f, 160f, 480f, 160f)
            quadTo(562f, 160f, 634f, 191f)
            quadTo(706f, 222f, 760f, 275.5f)
            quadTo(814f, 329f, 846f, 400.5f)
            quadTo(878f, 472f, 879f, 554f)
            quadTo(880f, 609f, 866.5f, 661.5f)
            quadTo(853f, 714f, 825f, 762f)
            quadTo(814f, 781f, 795.5f, 790.5f)
            quadTo(777f, 800f, 755f, 800f)
            lineTo(204f, 800f)
            close()
        }
    }.build()
}

val Icons.Outlined.Speed: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Speed",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(418f, 620f)
            quadToRelative(24f, 24f, 62f, 23.5f)
            reflectiveQuadToRelative(56f, -27.5f)
            lineToRelative(169f, -253f)
            quadToRelative(9f, -14f, -2.5f, -25.5f)
            reflectiveQuadTo(677f, 335f)
            lineTo(424f, 504f)
            quadToRelative(-27f, 18f, -28.5f, 55f)
            reflectiveQuadToRelative(22.5f, 61f)
            close()
            moveTo(480f, 160f)
            quadToRelative(36f, 0f, 71f, 6f)
            reflectiveQuadToRelative(68f, 19f)
            quadToRelative(16f, 6f, 34f, 22.5f)
            reflectiveQuadToRelative(10f, 31.5f)
            quadToRelative(-8f, 15f, -36f, 20f)
            reflectiveQuadToRelative(-45f, -1f)
            quadToRelative(-25f, -9f, -50.5f, -13.5f)
            reflectiveQuadTo(480f, 240f)
            quadToRelative(-133f, 0f, -226.5f, 93.5f)
            reflectiveQuadTo(160f, 560f)
            quadToRelative(0f, 42f, 11.5f, 83f)
            reflectiveQuadToRelative(32.5f, 77f)
            horizontalLineToRelative(552f)
            quadToRelative(23f, -38f, 33.5f, -79f)
            reflectiveQuadToRelative(10.5f, -85f)
            quadToRelative(0f, -26f, -4.5f, -51f)
            reflectiveQuadTo(782f, 456f)
            quadToRelative(-6f, -17f, -2f, -33f)
            reflectiveQuadToRelative(18f, -27f)
            quadToRelative(13f, -10f, 28.5f, -6f)
            reflectiveQuadToRelative(21.5f, 18f)
            quadToRelative(15f, 35f, 23f, 71.5f)
            reflectiveQuadToRelative(9f, 74.5f)
            quadToRelative(1f, 57f, -13f, 109f)
            reflectiveQuadToRelative(-41f, 99f)
            quadToRelative(-11f, 18f, -30f, 28f)
            reflectiveQuadToRelative(-40f, 10f)
            lineTo(204f, 800f)
            quadToRelative(-21f, 0f, -40f, -10f)
            reflectiveQuadToRelative(-30f, -28f)
            quadToRelative(-26f, -45f, -40f, -95.5f)
            reflectiveQuadTo(80f, 560f)
            quadToRelative(0f, -83f, 31.5f, -155.5f)
            reflectiveQuadToRelative(86f, -127f)
            quadTo(252f, 223f, 325f, 191.5f)
            reflectiveQuadTo(480f, 160f)
            close()
            moveTo(487f, 473f)
            close()
        }
    }.build()
}
