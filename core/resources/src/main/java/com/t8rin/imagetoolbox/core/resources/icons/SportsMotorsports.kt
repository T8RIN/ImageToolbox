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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.SportsMotorsports: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SportsMotorsports",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, -480f)
                close()
                moveToRelative(80f, 240f)
                quadToRelative(100f, 0f, 170f, -70f)
                reflectiveQuadToRelative(70f, -170f)
                quadToRelative(0f, -101f, -73.5f, -170.5f)
                reflectiveQuadTo(550f, -720f)
                quadToRelative(-48f, 0f, -93f, 11f)
                reflectiveQuadToRelative(-87f, 33f)
                lineToRelative(100f, 40f)
                quadToRelative(41f, 17f, 65.5f, 52.5f)
                reflectiveQuadTo(560f, -504f)
                quadToRelative(0f, 60f, -41.5f, 102f)
                reflectiveQuadTo(418f, -360f)
                horizontalLineTo(162f)
                quadToRelative(-2f, 24f, -2f, 54.5f)
                verticalLineToRelative(65.5f)
                horizontalLineToRelative(400f)
                close()
                moveTo(176f, -440f)
                horizontalLineToRelative(240f)
                quadToRelative(27f, 0f, 45.5f, -18.5f)
                reflectiveQuadTo(480f, -504f)
                quadToRelative(0f, -19f, -10.5f, -34.5f)
                reflectiveQuadTo(440f, -562f)
                lineToRelative(-148f, -60f)
                quadToRelative(-42f, 37f, -71.5f, 84f)
                reflectiveQuadTo(176f, -440f)
                close()
                moveToRelative(384f, 280f)
                horizontalLineTo(160f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(80f, -240f)
                verticalLineToRelative(-90f)
                quadToRelative(0f, -98f, 37f, -183.5f)
                reflectiveQuadToRelative(100.5f, -149f)
                quadTo(281f, -726f, 367f, -763f)
                reflectiveQuadToRelative(183f, -37f)
                quadToRelative(68f, 0f, 128f, 25f)
                reflectiveQuadToRelative(105f, 68.5f)
                quadTo(828f, -663f, 854f, -605f)
                reflectiveQuadToRelative(26f, 125f)
                quadToRelative(0f, 66f, -25f, 124.5f)
                reflectiveQuadToRelative(-68.5f, 102f)
                quadTo(743f, -210f, 684.5f, -185f)
                reflectiveQuadTo(560f, -160f)
                close()
            }
        }
    }.build()
}