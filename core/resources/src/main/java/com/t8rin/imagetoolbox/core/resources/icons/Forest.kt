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

val Icons.Outlined.Forest: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Forest",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(280f, -120f)
                verticalLineToRelative(-120f)
                horizontalLineTo(73f)
                quadToRelative(-24f, 0f, -35f, -21f)
                reflectiveQuadToRelative(2f, -41f)
                lineToRelative(114f, -178f)
                quadToRelative(-23f, 0f, -34.5f, -20.5f)
                reflectiveQuadTo(122f, -541f)
                lineToRelative(205f, -292f)
                quadToRelative(12f, -17f, 33f, -17f)
                reflectiveQuadToRelative(33f, 17f)
                lineToRelative(87f, 125f)
                lineToRelative(87f, -125f)
                quadToRelative(12f, -17f, 33f, -17f)
                reflectiveQuadToRelative(33f, 17f)
                lineToRelative(205f, 292f)
                quadToRelative(14f, 20f, 2.5f, 40.5f)
                reflectiveQuadTo(806f, -480f)
                lineToRelative(114f, 178f)
                quadToRelative(13f, 20f, 2f, 41f)
                reflectiveQuadToRelative(-35f, 21f)
                horizontalLineTo(680f)
                verticalLineToRelative(120f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(640f, -80f)
                horizontalLineToRelative(-80f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(520f, -120f)
                verticalLineToRelative(-120f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(120f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(400f, -80f)
                horizontalLineToRelative(-80f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(280f, -120f)
                close()
                moveToRelative(389f, -200f)
                horizontalLineToRelative(145f)
                lineTo(659f, -560f)
                horizontalLineToRelative(67f)
                lineTo(600f, -740f)
                lineToRelative(-71f, 101f)
                lineToRelative(69f, 98f)
                quadToRelative(14f, 20f, 2.5f, 40.5f)
                reflectiveQuadTo(566f, -480f)
                lineToRelative(103f, 160f)
                close()
                moveToRelative(-523f, 0f)
                horizontalLineToRelative(428f)
                lineTo(419f, -560f)
                horizontalLineToRelative(67f)
                lineTo(360f, -740f)
                lineTo(234f, -560f)
                horizontalLineToRelative(67f)
                lineTo(146f, -320f)
                close()
                moveToRelative(0f, 0f)
                horizontalLineToRelative(155f)
                horizontalLineToRelative(-67f)
                horizontalLineToRelative(252f)
                horizontalLineToRelative(-67f)
                horizontalLineToRelative(155f)
                horizontalLineToRelative(-428f)
                close()
                moveToRelative(523f, 0f)
                horizontalLineTo(566f)
                horizontalLineToRelative(74f)
                horizontalLineToRelative(-111f)
                horizontalLineToRelative(197f)
                horizontalLineToRelative(-67f)
                horizontalLineToRelative(155f)
                horizontalLineToRelative(-145f)
                close()
                moveToRelative(-149f, 80f)
                horizontalLineToRelative(160f)
                horizontalLineToRelative(-160f)
                close()
                moveToRelative(201f, 0f)
                close()
            }
        }
    }.build()
}