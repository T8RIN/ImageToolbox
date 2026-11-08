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

val Icons.Outlined.SportsBasketball: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SportsBasketball",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(162f, -520f)
                horizontalLineToRelative(114f)
                quadToRelative(-6f, -38f, -23f, -71f)
                reflectiveQuadToRelative(-43f, -59f)
                quadToRelative(-18f, 29f, -30.5f, 61.5f)
                reflectiveQuadTo(162f, -520f)
                close()
                moveToRelative(522f, 0f)
                horizontalLineToRelative(114f)
                quadToRelative(-5f, -36f, -17.5f, -68.5f)
                reflectiveQuadTo(750f, -650f)
                quadToRelative(-26f, 26f, -43f, 59f)
                reflectiveQuadToRelative(-23f, 71f)
                close()
                moveTo(210f, -310f)
                quadToRelative(26f, -26f, 43f, -59f)
                reflectiveQuadToRelative(23f, -71f)
                horizontalLineTo(162f)
                quadToRelative(5f, 36f, 17.5f, 68.5f)
                reflectiveQuadTo(210f, -310f)
                close()
                moveToRelative(540f, 0f)
                quadToRelative(18f, -29f, 30.5f, -61.5f)
                reflectiveQuadTo(798f, -440f)
                horizontalLineTo(684f)
                quadToRelative(6f, 38f, 23f, 71f)
                reflectiveQuadToRelative(43f, 59f)
                close()
                moveTo(358f, -520f)
                horizontalLineToRelative(82f)
                verticalLineToRelative(-278f)
                quadToRelative(-53f, 8f, -98.5f, 29.5f)
                reflectiveQuadTo(260f, -712f)
                quadToRelative(39f, 38f, 64.5f, 86.5f)
                reflectiveQuadTo(358f, -520f)
                close()
                moveToRelative(162f, 0f)
                horizontalLineToRelative(82f)
                quadToRelative(8f, -57f, 33.5f, -105.5f)
                reflectiveQuadTo(700f, -712f)
                quadToRelative(-36f, -35f, -81.5f, -56.5f)
                reflectiveQuadTo(520f, -798f)
                verticalLineToRelative(278f)
                close()
                moveToRelative(-80f, 358f)
                verticalLineToRelative(-278f)
                horizontalLineToRelative(-82f)
                quadToRelative(-8f, 57f, -33.5f, 105.5f)
                reflectiveQuadTo(260f, -248f)
                quadToRelative(36f, 35f, 81.5f, 56.5f)
                reflectiveQuadTo(440f, -162f)
                close()
                moveToRelative(80f, 0f)
                quadToRelative(53f, -8f, 98.5f, -29.5f)
                reflectiveQuadTo(700f, -248f)
                quadToRelative(-39f, -38f, -64.5f, -86.5f)
                reflectiveQuadTo(602f, -440f)
                horizontalLineToRelative(-82f)
                verticalLineToRelative(278f)
                close()
                moveToRelative(-40f, -318f)
                close()
                moveToRelative(0f, 400f)
                quadToRelative(-83f, 0f, -156f, -31.5f)
                reflectiveQuadTo(197f, -197f)
                quadToRelative(-54f, -54f, -85.5f, -127f)
                reflectiveQuadTo(80f, -480f)
                quadToRelative(0f, -83f, 31.5f, -156f)
                reflectiveQuadTo(197f, -763f)
                quadToRelative(54f, -54f, 127f, -85.5f)
                reflectiveQuadTo(480f, -880f)
                quadToRelative(83f, 0f, 156f, 31.5f)
                reflectiveQuadTo(763f, -763f)
                quadToRelative(54f, 54f, 85.5f, 127f)
                reflectiveQuadTo(880f, -480f)
                quadToRelative(0f, 83f, -31.5f, 156f)
                reflectiveQuadTo(763f, -197f)
                quadToRelative(-54f, 54f, -127f, 85.5f)
                reflectiveQuadTo(480f, -80f)
                close()
            }
        }
    }.build()
}