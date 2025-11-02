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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.EditAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.EditAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000))
        ) {
            moveTo(200f, 760f)
            horizontalLineToRelative(57f)
            lineToRelative(391f, -391f)
            lineToRelative(-57f, -57f)
            lineToRelative(-391f, 391f)
            verticalLineToRelative(57f)
            close()
            moveTo(160f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(120f, 800f)
            verticalLineToRelative(-97f)
            quadToRelative(0f, -16f, 6f, -30.5f)
            reflectiveQuadToRelative(17f, -25.5f)
            lineToRelative(505f, -504f)
            quadToRelative(12f, -11f, 26.5f, -17f)
            reflectiveQuadToRelative(30.5f, -6f)
            quadToRelative(16f, 0f, 31f, 6f)
            reflectiveQuadToRelative(26f, 18f)
            lineToRelative(55f, 56f)
            quadToRelative(12f, 11f, 17.5f, 26f)
            reflectiveQuadToRelative(5.5f, 30f)
            quadToRelative(0f, 16f, -5.5f, 30.5f)
            reflectiveQuadTo(817f, 313f)
            lineTo(313f, 817f)
            quadToRelative(-11f, 11f, -25.5f, 17f)
            reflectiveQuadToRelative(-30.5f, 6f)
            horizontalLineToRelative(-97f)
            close()
            moveTo(760f, 256f)
            lineTo(704f, 200f)
            lineTo(760f, 256f)
            close()
            moveTo(619f, 341f)
            lineTo(591f, 312f)
            lineTo(648f, 369f)
            lineTo(619f, 341f)
            close()
        }
    }.build()
}

val Icons.Filled.EditAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Filled.EditAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(160f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(120f, 800f)
            verticalLineToRelative(-97f)
            quadToRelative(0f, -16f, 6f, -30.5f)
            reflectiveQuadToRelative(17f, -25.5f)
            lineToRelative(505f, -504f)
            quadToRelative(12f, -11f, 26.5f, -17f)
            reflectiveQuadToRelative(30.5f, -6f)
            quadToRelative(16f, 0f, 31f, 6f)
            reflectiveQuadToRelative(26f, 18f)
            lineToRelative(55f, 56f)
            quadToRelative(12f, 11f, 17.5f, 26f)
            reflectiveQuadToRelative(5.5f, 30f)
            quadToRelative(0f, 16f, -5.5f, 30.5f)
            reflectiveQuadTo(817f, 313f)
            lineTo(313f, 817f)
            quadToRelative(-11f, 11f, -25.5f, 17f)
            reflectiveQuadToRelative(-30.5f, 6f)
            horizontalLineToRelative(-97f)
            close()
            moveTo(704f, 312f)
            lineTo(760f, 256f)
            lineTo(704f, 200f)
            lineTo(648f, 256f)
            lineTo(704f, 312f)
            close()
        }
    }.build()
}