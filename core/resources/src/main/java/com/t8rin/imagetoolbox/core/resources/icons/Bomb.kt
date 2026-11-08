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

val Icons.Outlined.Bomb: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Bomb",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(346f, -128f)
                quadToRelative(91f, 0f, 155.5f, -64.5f)
                reflectiveQuadTo(566f, -348f)
                quadToRelative(0f, -31f, -8.5f, -61f)
                reflectiveQuadTo(532f, -466f)
                lineToRelative(-26f, -41f)
                lineToRelative(42f, -72f)
                lineToRelative(-104f, -60f)
                lineToRelative(-42f, 72f)
                horizontalLineToRelative(-44f)
                quadToRelative(-94f, 0f, -163.5f, 60f)
                reflectiveQuadTo(125f, -350f)
                quadToRelative(0f, 92f, 64.5f, 157f)
                reflectiveQuadTo(346f, -128f)
                close()
                moveToRelative(494f, -480f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(800f, -648f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(840f, -688f)
                horizontalLineToRelative(40f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(920f, -648f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(880f, -608f)
                horizontalLineToRelative(-40f)
                close()
                moveTo(620f, -828f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(580f, -868f)
                verticalLineToRelative(-40f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(620f, -948f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(660f, -908f)
                verticalLineToRelative(40f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(620f, -828f)
                close()
                moveToRelative(127f, 53f)
                quadToRelative(-11f, -11f, -11f, -28f)
                reflectiveQuadToRelative(11f, -28f)
                lineToRelative(29f, -29f)
                quadToRelative(11f, -11f, 28f, -11f)
                reflectiveQuadToRelative(28f, 11f)
                quadToRelative(11f, 11f, 11f, 28f)
                reflectiveQuadToRelative(-11f, 28f)
                lineToRelative(-29f, 29f)
                quadToRelative(-11f, 11f, -28f, 11f)
                reflectiveQuadToRelative(-28f, -11f)
                close()
                moveTo(346f, -348f)
                close()
                moveToRelative(334f, -310f)
                quadToRelative(-5f, 0f, -10f, -1f)
                reflectiveQuadToRelative(-10f, -4f)
                lineToRelative(-35f, -20f)
                quadToRelative(-14f, -8f, -30.5f, -3.5f)
                reflectiveQuadTo(570f, -668f)
                lineToRelative(-5f, 8f)
                lineToRelative(40f, 23f)
                quadToRelative(21f, 12f, 27.5f, 36f)
                reflectiveQuadToRelative(-5.5f, 45f)
                lineToRelative(-27f, 48f)
                quadToRelative(23f, 36f, 34.5f, 76.5f)
                reflectiveQuadTo(646f, -348f)
                quadToRelative(0f, 125f, -87.5f, 212.5f)
                reflectiveQuadTo(346f, -48f)
                quadToRelative(-125f, 0f, -212.5f, -88.5f)
                reflectiveQuadTo(46f, -350f)
                quadToRelative(0f, -125f, 86.5f, -211.5f)
                reflectiveQuadTo(344f, -648f)
                horizontalLineToRelative(13f)
                lineToRelative(27f, -47f)
                quadToRelative(12f, -22f, 36f, -28.5f)
                reflectiveQuadToRelative(46f, 6.5f)
                lineToRelative(30f, 17f)
                lineToRelative(5f, -8f)
                quadToRelative(23f, -43f, 72f, -56f)
                reflectiveQuadToRelative(92f, 12f)
                lineToRelative(34f, 19f)
                quadToRelative(9f, 5f, 15f, 14.5f)
                reflectiveQuadToRelative(6f, 20.5f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(680f, -658f)
                close()
            }
        }
    }.build()
}