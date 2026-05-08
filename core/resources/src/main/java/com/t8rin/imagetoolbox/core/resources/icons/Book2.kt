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

package com.t8rin.imagetoolbox.core.resources

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Book2: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Book2",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(240f, 613f)
            quadToRelative(14f, -7f, 29f, -10f)
            reflectiveQuadToRelative(31f, -3f)
            horizontalLineToRelative(20f)
            verticalLineToRelative(-440f)
            horizontalLineToRelative(-20f)
            quadToRelative(-25f, 0f, -42.5f, 17.5f)
            reflectiveQuadTo(240f, 220f)
            verticalLineToRelative(393f)
            close()
            moveTo(400f, 600f)
            horizontalLineToRelative(320f)
            verticalLineToRelative(-440f)
            lineTo(400f, 160f)
            verticalLineToRelative(440f)
            close()
            moveTo(240f, 613f)
            verticalLineToRelative(-453f)
            verticalLineToRelative(453f)
            close()
            moveTo(300f, 880f)
            quadToRelative(-58f, 0f, -99f, -41f)
            reflectiveQuadToRelative(-41f, -99f)
            verticalLineToRelative(-520f)
            quadToRelative(0f, -58f, 41f, -99f)
            reflectiveQuadToRelative(99f, -41f)
            horizontalLineToRelative(420f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(800f, 160f)
            verticalLineToRelative(501f)
            quadToRelative(0f, 8f, -6.5f, 14.5f)
            reflectiveQuadTo(770f, 690f)
            quadToRelative(-14f, 7f, -22f, 20f)
            reflectiveQuadToRelative(-8f, 30f)
            quadToRelative(0f, 17f, 8f, 30.5f)
            reflectiveQuadToRelative(22f, 19.5f)
            quadToRelative(14f, 6f, 22f, 16.5f)
            reflectiveQuadToRelative(8f, 22.5f)
            verticalLineToRelative(10f)
            quadToRelative(0f, 17f, -11.5f, 29f)
            reflectiveQuadTo(760f, 880f)
            lineTo(300f, 880f)
            close()
            moveTo(300f, 800f)
            horizontalLineToRelative(373f)
            quadToRelative(-6f, -14f, -9.5f, -28.5f)
            reflectiveQuadTo(660f, 740f)
            quadToRelative(0f, -16f, 3f, -31f)
            reflectiveQuadToRelative(10f, -29f)
            lineTo(300f, 680f)
            quadToRelative(-26f, 0f, -43f, 17.5f)
            reflectiveQuadTo(240f, 740f)
            quadToRelative(0f, 26f, 17f, 43f)
            reflectiveQuadToRelative(43f, 17f)
            close()
        }
    }.build()
}
