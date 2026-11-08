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

val Icons.Outlined.Balance: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Balance",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(120f, -120f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(80f, -160f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(120f, -200f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(-447f)
                quadToRelative(-26f, -9f, -45f, -28f)
                reflectiveQuadToRelative(-28f, -45f)
                horizontalLineTo(240f)
                lineToRelative(110f, 258f)
                quadToRelative(5f, 11f, 6f, 22.5f)
                reflectiveQuadToRelative(-1f, 23.5f)
                quadToRelative(-9f, 46f, -49.5f, 71f)
                reflectiveQuadTo(220f, -320f)
                quadToRelative(-45f, 0f, -85.5f, -25f)
                reflectiveQuadTo(85f, -416f)
                quadToRelative(-2f, -12f, -1f, -23.5f)
                reflectiveQuadToRelative(6f, -22.5f)
                lineToRelative(110f, -258f)
                horizontalLineToRelative(-40f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(120f, -760f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(160f, -800f)
                horizontalLineToRelative(207f)
                quadToRelative(12f, -35f, 43f, -57.5f)
                reflectiveQuadToRelative(70f, -22.5f)
                quadToRelative(39f, 0f, 70f, 22.5f)
                reflectiveQuadToRelative(43f, 57.5f)
                horizontalLineToRelative(207f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(840f, -760f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(800f, -720f)
                horizontalLineToRelative(-40f)
                lineToRelative(110f, 258f)
                quadToRelative(5f, 11f, 6f, 22.5f)
                reflectiveQuadToRelative(-1f, 23.5f)
                quadToRelative(-9f, 46f, -49.5f, 71f)
                reflectiveQuadTo(740f, -320f)
                quadToRelative(-45f, 0f, -85.5f, -25f)
                reflectiveQuadTo(605f, -416f)
                quadToRelative(-2f, -12f, -1f, -23.5f)
                reflectiveQuadToRelative(6f, -22.5f)
                lineToRelative(110f, -258f)
                horizontalLineTo(593f)
                quadToRelative(-9f, 26f, -28f, 45f)
                reflectiveQuadToRelative(-45f, 28f)
                verticalLineToRelative(447f)
                horizontalLineToRelative(320f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(880f, -160f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(840f, -120f)
                horizontalLineTo(120f)
                close()
                moveToRelative(545f, -320f)
                horizontalLineToRelative(150f)
                lineToRelative(-75f, -174f)
                lineToRelative(-75f, 174f)
                close()
                moveToRelative(-520f, 0f)
                horizontalLineToRelative(150f)
                lineToRelative(-75f, -174f)
                lineToRelative(-75f, 174f)
                close()
                moveToRelative(335f, -280f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(520f, -760f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(480f, -800f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(440f, -760f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(480f, -720f)
                close()
            }
        }
    }.build()
}