/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Stacks: ImageVector by lazy {
    Builder(
        name = "Stacks", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(480.0f, 549.0f)
            quadToRelative(-10.0f, 0.0f, -19.5f, -2.5f)
            reflectiveQuadTo(442.0f, 539.0f)
            lineTo(104.0f, 355.0f)
            quadToRelative(-11.0f, -6.0f, -15.5f, -15.0f)
            reflectiveQuadTo(84.0f, 320.0f)
            quadToRelative(0.0f, -11.0f, 4.5f, -20.0f)
            reflectiveQuadToRelative(15.5f, -15.0f)
            lineToRelative(338.0f, -184.0f)
            quadToRelative(9.0f, -5.0f, 18.5f, -7.5f)
            reflectiveQuadTo(480.0f, 91.0f)
            quadToRelative(10.0f, 0.0f, 19.5f, 2.5f)
            reflectiveQuadTo(518.0f, 101.0f)
            lineToRelative(338.0f, 184.0f)
            quadToRelative(11.0f, 6.0f, 15.5f, 15.0f)
            reflectiveQuadToRelative(4.5f, 20.0f)
            quadToRelative(0.0f, 11.0f, -4.5f, 20.0f)
            reflectiveQuadTo(856.0f, 355.0f)
            lineTo(518.0f, 539.0f)
            quadToRelative(-9.0f, 5.0f, -18.5f, 7.5f)
            reflectiveQuadTo(480.0f, 549.0f)
            close()
            moveTo(480.0f, 629.0f)
            lineTo(794.0f, 458.0f)
            quadToRelative(2.0f, -1.0f, 19.0f, -5.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(853.0f, 493.0f)
            quadToRelative(0.0f, 11.0f, -5.0f, 20.0f)
            reflectiveQuadToRelative(-16.0f, 15.0f)
            lineTo(518.0f, 699.0f)
            quadToRelative(-9.0f, 5.0f, -18.5f, 7.5f)
            reflectiveQuadTo(480.0f, 709.0f)
            quadToRelative(-10.0f, 0.0f, -19.5f, -2.5f)
            reflectiveQuadTo(442.0f, 699.0f)
            lineTo(128.0f, 528.0f)
            quadToRelative(-11.0f, -6.0f, -16.0f, -15.0f)
            reflectiveQuadToRelative(-5.0f, -20.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(147.0f, 453.0f)
            quadToRelative(5.0f, 0.0f, 9.5f, 1.5f)
            reflectiveQuadToRelative(9.5f, 3.5f)
            lineToRelative(314.0f, 171.0f)
            close()
            moveTo(480.0f, 789.0f)
            lineTo(794.0f, 618.0f)
            quadToRelative(2.0f, -1.0f, 19.0f, -5.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(853.0f, 653.0f)
            quadToRelative(0.0f, 11.0f, -5.0f, 20.0f)
            reflectiveQuadToRelative(-16.0f, 15.0f)
            lineTo(518.0f, 859.0f)
            quadToRelative(-9.0f, 5.0f, -18.5f, 7.5f)
            reflectiveQuadTo(480.0f, 869.0f)
            quadToRelative(-10.0f, 0.0f, -19.5f, -2.5f)
            reflectiveQuadTo(442.0f, 859.0f)
            lineTo(128.0f, 688.0f)
            quadToRelative(-11.0f, -6.0f, -16.0f, -15.0f)
            reflectiveQuadToRelative(-5.0f, -20.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(147.0f, 613.0f)
            quadToRelative(5.0f, 0.0f, 9.5f, 1.5f)
            reflectiveQuadToRelative(9.5f, 3.5f)
            lineToRelative(314.0f, 171.0f)
            close()
        }
    }
        .build()
}