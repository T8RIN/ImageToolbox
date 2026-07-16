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
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.BookmarkStar: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.BookmarkStar",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(480f, 505f)
            lineToRelative(51f, 31f)
            quadToRelative(11f, 7f, 21.5f, -1f)
            reflectiveQuadToRelative(7.5f, -21f)
            lineToRelative(-13f, -58f)
            lineToRelative(44f, -38f)
            quadToRelative(10f, -9f, 6.5f, -21f)
            reflectiveQuadTo(580f, 383f)
            lineToRelative(-58f, -5f)
            lineToRelative(-24f, -55f)
            quadToRelative(-5f, -12f, -18f, -12f)
            reflectiveQuadToRelative(-18f, 12f)
            lineToRelative(-24f, 55f)
            lineToRelative(-58f, 5f)
            quadToRelative(-14f, 2f, -17.5f, 14f)
            reflectiveQuadToRelative(6.5f, 21f)
            lineToRelative(44f, 38f)
            lineToRelative(-13f, 58f)
            quadToRelative(-3f, 13f, 7.5f, 21f)
            reflectiveQuadToRelative(21.5f, 1f)
            lineToRelative(51f, -31f)
            close()
            moveTo(480f, 720f)
            lineTo(312f, 792f)
            quadToRelative(-40f, 17f, -76f, -6.5f)
            reflectiveQuadTo(200f, 719f)
            verticalLineToRelative(-519f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 120f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 200f)
            verticalLineToRelative(519f)
            quadToRelative(0f, 43f, -36f, 66.5f)
            reflectiveQuadToRelative(-76f, 6.5f)
            lineToRelative(-168f, -72f)
            close()
            moveTo(480f, 632f)
            lineTo(680f, 718f)
            verticalLineToRelative(-518f)
            lineTo(280f, 200f)
            verticalLineToRelative(518f)
            lineToRelative(200f, -86f)
            close()
            moveTo(480f, 200f)
            lineTo(280f, 200f)
            horizontalLineToRelative(400f)
            horizontalLineToRelative(-200f)
            close()
        }
    }.build()
}

val Icons.Rounded.BookmarkStar: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.BookmarkStar",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(480f, 505f)
            lineToRelative(51f, 31f)
            quadToRelative(11f, 7f, 21.5f, -1f)
            reflectiveQuadToRelative(7.5f, -21f)
            lineToRelative(-13f, -58f)
            lineToRelative(44f, -38f)
            quadToRelative(10f, -9f, 6.5f, -21f)
            reflectiveQuadTo(580f, 383f)
            lineToRelative(-58f, -5f)
            lineToRelative(-24f, -55f)
            quadToRelative(-5f, -12f, -18f, -12f)
            reflectiveQuadToRelative(-18f, 12f)
            lineToRelative(-24f, 55f)
            lineToRelative(-58f, 5f)
            quadToRelative(-14f, 2f, -17.5f, 14f)
            reflectiveQuadToRelative(6.5f, 21f)
            lineToRelative(44f, 38f)
            lineToRelative(-13f, 58f)
            quadToRelative(-3f, 13f, 7.5f, 21f)
            reflectiveQuadToRelative(21.5f, 1f)
            lineToRelative(51f, -31f)
            close()
            moveTo(480f, 720f)
            lineTo(312f, 792f)
            quadToRelative(-40f, 17f, -76f, -6.5f)
            reflectiveQuadTo(200f, 719f)
            verticalLineToRelative(-519f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 120f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 200f)
            verticalLineToRelative(519f)
            quadToRelative(0f, 43f, -36f, 66.5f)
            reflectiveQuadToRelative(-76f, 6.5f)
            lineToRelative(-168f, -72f)
            close()
        }
    }.build()
}