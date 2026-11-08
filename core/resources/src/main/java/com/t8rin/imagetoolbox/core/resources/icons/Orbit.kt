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

val Icons.Outlined.Orbit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Orbit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(240f, -100f)
                quadToRelative(-58f, 0f, -99f, -41f)
                reflectiveQuadToRelative(-41f, -99f)
                quadToRelative(0f, -58f, 41f, -99f)
                reflectiveQuadToRelative(99f, -41f)
                quadToRelative(58f, 0f, 99f, 41f)
                reflectiveQuadToRelative(41f, 99f)
                quadToRelative(0f, 22f, -6.5f, 42.5f)
                reflectiveQuadTo(354f, -159f)
                verticalLineToRelative(-27f)
                quadToRelative(30f, 13f, 62f, 19.5f)
                reflectiveQuadToRelative(64f, 6.5f)
                quadToRelative(123f, 0f, 212.5f, -80f)
                reflectiveQuadTo(797f, -439f)
                quadToRelative(2f, -17f, 14f, -29f)
                reflectiveQuadToRelative(29f, -12f)
                quadToRelative(17f, 0f, 28.5f, 12f)
                reflectiveQuadToRelative(9.5f, 29f)
                quadToRelative(-16f, 152f, -129f, 255.5f)
                reflectiveQuadTo(480f, -80f)
                quadToRelative(-45f, 0f, -88f, -9.5f)
                reflectiveQuadTo(309f, -118f)
                quadToRelative(-16f, 9f, -33.5f, 13.5f)
                reflectiveQuadTo(240f, -100f)
                close()
                moveToRelative(0f, -80f)
                quadToRelative(25f, 0f, 42.5f, -17.5f)
                reflectiveQuadTo(300f, -240f)
                quadToRelative(0f, -25f, -17.5f, -42.5f)
                reflectiveQuadTo(240f, -300f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(180f, -240f)
                quadToRelative(0f, 25f, 17.5f, 42.5f)
                reflectiveQuadTo(240f, -180f)
                close()
                moveToRelative(240f, -160f)
                quadToRelative(-58f, 0f, -99f, -41f)
                reflectiveQuadToRelative(-41f, -99f)
                quadToRelative(0f, -58f, 41f, -99f)
                reflectiveQuadToRelative(99f, -41f)
                quadToRelative(58f, 0f, 99f, 41f)
                reflectiveQuadToRelative(41f, 99f)
                quadToRelative(0f, 58f, -41f, 99f)
                reflectiveQuadToRelative(-99f, 41f)
                close()
                moveToRelative(0f, -460f)
                quadToRelative(-123f, 0f, -212.5f, 80f)
                reflectiveQuadTo(163f, -521f)
                quadToRelative(-2f, 17f, -14f, 29f)
                reflectiveQuadToRelative(-29f, 12f)
                quadToRelative(-17f, 0f, -28.5f, -12f)
                reflectiveQuadTo(82f, -521f)
                quadToRelative(16f, -152f, 129f, -255.5f)
                reflectiveQuadTo(480f, -880f)
                quadToRelative(45f, 0f, 88f, 9.5f)
                reflectiveQuadToRelative(83f, 28.5f)
                quadToRelative(16f, -9f, 33.5f, -13.5f)
                reflectiveQuadTo(720f, -860f)
                quadToRelative(58f, 0f, 99f, 41f)
                reflectiveQuadToRelative(41f, 99f)
                quadToRelative(0f, 58f, -41f, 99f)
                reflectiveQuadToRelative(-99f, 41f)
                quadToRelative(-58f, 0f, -99f, -41f)
                reflectiveQuadToRelative(-41f, -99f)
                quadToRelative(0f, -22f, 6.5f, -42.5f)
                reflectiveQuadTo(606f, -801f)
                verticalLineToRelative(27f)
                quadToRelative(-30f, -13f, -62f, -19.5f)
                reflectiveQuadToRelative(-64f, -6.5f)
                close()
                moveToRelative(240f, 140f)
                quadToRelative(25f, 0f, 42.5f, -17.5f)
                reflectiveQuadTo(780f, -720f)
                quadToRelative(0f, -25f, -17.5f, -42.5f)
                reflectiveQuadTo(720f, -780f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(660f, -720f)
                quadToRelative(0f, 25f, 17.5f, 42.5f)
                reflectiveQuadTo(720f, -660f)
                close()
                moveTo(240f, -240f)
                close()
                moveToRelative(480f, -480f)
                close()
            }
        }
    }.build()
}