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

val Icons.Outlined.Badge: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Badge",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(240f, 720f)
            horizontalLineToRelative(240f)
            verticalLineToRelative(-18f)
            quadToRelative(0f, -17f, -9.5f, -31.5f)
            reflectiveQuadTo(444f, 648f)
            quadToRelative(-20f, -9f, -40.5f, -13.5f)
            reflectiveQuadTo(360f, 630f)
            quadToRelative(-23f, 0f, -43.5f, 4.5f)
            reflectiveQuadTo(276f, 648f)
            quadToRelative(-17f, 8f, -26.5f, 22.5f)
            reflectiveQuadTo(240f, 702f)
            verticalLineToRelative(18f)
            close()
            moveTo(590f, 660f)
            horizontalLineToRelative(100f)
            quadToRelative(13f, 0f, 21.5f, -8.5f)
            reflectiveQuadTo(720f, 630f)
            quadToRelative(0f, -13f, -8.5f, -21.5f)
            reflectiveQuadTo(690f, 600f)
            lineTo(590f, 600f)
            quadToRelative(-13f, 0f, -21.5f, 8.5f)
            reflectiveQuadTo(560f, 630f)
            quadToRelative(0f, 13f, 8.5f, 21.5f)
            reflectiveQuadTo(590f, 660f)
            close()
            moveTo(360f, 600f)
            quadToRelative(25f, 0f, 42.5f, -17.5f)
            reflectiveQuadTo(420f, 540f)
            quadToRelative(0f, -25f, -17.5f, -42.5f)
            reflectiveQuadTo(360f, 480f)
            quadToRelative(-25f, 0f, -42.5f, 17.5f)
            reflectiveQuadTo(300f, 540f)
            quadToRelative(0f, 25f, 17.5f, 42.5f)
            reflectiveQuadTo(360f, 600f)
            close()
            moveTo(590f, 540f)
            horizontalLineToRelative(100f)
            quadToRelative(13f, 0f, 21.5f, -8.5f)
            reflectiveQuadTo(720f, 510f)
            quadToRelative(0f, -13f, -8.5f, -21.5f)
            reflectiveQuadTo(690f, 480f)
            lineTo(590f, 480f)
            quadToRelative(-13f, 0f, -21.5f, 8.5f)
            reflectiveQuadTo(560f, 510f)
            quadToRelative(0f, 13f, 8.5f, 21.5f)
            reflectiveQuadTo(590f, 540f)
            close()
            moveTo(160f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 800f)
            verticalLineToRelative(-440f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 280f)
            horizontalLineToRelative(200f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(440f, 80f)
            horizontalLineToRelative(80f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(600f, 160f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(200f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 360f)
            verticalLineToRelative(440f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 880f)
            lineTo(160f, 880f)
            close()
            moveTo(160f, 800f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-440f)
            lineTo(600f, 360f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(520f, 440f)
            horizontalLineToRelative(-80f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(360f, 360f)
            lineTo(160f, 360f)
            verticalLineToRelative(440f)
            close()
            moveTo(440f, 360f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-200f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(200f)
            close()
            moveTo(480f, 580f)
            close()
        }
    }.build()
}
