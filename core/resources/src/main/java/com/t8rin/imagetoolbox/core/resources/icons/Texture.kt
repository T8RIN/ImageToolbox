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

val Icons.Outlined.Texture: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Texture",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(439f, 828f)
            quadToRelative(-11f, -11f, -12.5f, -26.5f)
            reflectiveQuadTo(439f, 772f)
            lineToRelative(333f, -333f)
            quadToRelative(14f, -14f, 29.5f, -12.5f)
            reflectiveQuadTo(828f, 439f)
            quadToRelative(13f, 13f, 12f, 29f)
            reflectiveQuadToRelative(-13f, 28f)
            lineTo(495f, 828f)
            quadToRelative(-12f, 12f, -28f, 12f)
            reflectiveQuadToRelative(-28f, -12f)
            close()
            moveTo(728f, 840f)
            quadToRelative(-14f, 0f, -19f, -12f)
            reflectiveQuadToRelative(5f, -22f)
            lineToRelative(92f, -92f)
            quadToRelative(10f, -10f, 22f, -5f)
            reflectiveQuadToRelative(12f, 19f)
            verticalLineToRelative(72f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 840f)
            horizontalLineToRelative(-72f)
            close()
            moveTo(131f, 829f)
            quadToRelative(-11f, -11f, -12f, -27f)
            reflectiveQuadToRelative(13f, -30f)
            lineToRelative(641f, -641f)
            quadToRelative(15f, -15f, 31f, -13f)
            reflectiveQuadToRelative(27f, 13f)
            quadToRelative(11f, 11f, 12f, 27f)
            reflectiveQuadToRelative(-14f, 30f)
            lineTo(187f, 829f)
            quadToRelative(-14f, 14f, -29.5f, 12.5f)
            reflectiveQuadTo(131f, 829f)
            close()
            moveTo(131f, 521f)
            quadToRelative(-11f, -11f, -12f, -27f)
            reflectiveQuadToRelative(13f, -30f)
            lineToRelative(332f, -332f)
            quadToRelative(14f, -14f, 29.5f, -12.5f)
            reflectiveQuadTo(520f, 132f)
            quadToRelative(11f, 11f, 12.5f, 26.5f)
            reflectiveQuadTo(520f, 188f)
            lineTo(187f, 521f)
            quadToRelative(-14f, 14f, -29.5f, 12.5f)
            reflectiveQuadTo(131f, 521f)
            close()
            moveTo(120f, 232f)
            verticalLineToRelative(-72f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(160f, 120f)
            horizontalLineToRelative(72f)
            quadToRelative(14f, 0f, 19f, 12f)
            reflectiveQuadToRelative(-5f, 22f)
            lineToRelative(-92f, 92f)
            quadToRelative(-10f, 10f, -22f, 5f)
            reflectiveQuadToRelative(-12f, -19f)
            close()
        }
    }.build()
}
