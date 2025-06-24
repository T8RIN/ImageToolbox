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
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Stack: ImageVector by lazy {
    Builder(
        name = "Stack", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960f, viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 560f)
            lineTo(40f, 320f)
            lineTo(480f, 80f)
            lineTo(920f, 320f)
            lineTo(480f, 560f)
            close()
            moveTo(480f, 720f)
            lineTo(63f, 493f)
            lineTo(147f, 447f)
            lineTo(480f, 629f)
            lineTo(813f, 447f)
            lineTo(897f, 493f)
            lineTo(480f, 720f)
            close()
            moveTo(480f, 880f)
            lineTo(63f, 653f)
            lineTo(147f, 607f)
            lineTo(480f, 789f)
            lineTo(813f, 607f)
            lineTo(897f, 653f)
            lineTo(480f, 880f)
            close()
            moveTo(480f, 469f)
            lineTo(753f, 320f)
            lineTo(480f, 171f)
            lineTo(207f, 320f)
            lineTo(480f, 469f)
            close()
            moveTo(480f, 320f)
            lineTo(480f, 320f)
            lineTo(480f, 320f)
            lineTo(480f, 320f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Stack: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "TwoTone.Stack",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 18f)
            lineToRelative(-10.425f, -5.675f)
            lineToRelative(2.1f, -1.15f)
            lineToRelative(8.325f, 4.55f)
            lineToRelative(8.325f, -4.55f)
            lineToRelative(2.1f, 1.15f)
            lineToRelative(-10.425f, 5.675f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 22f)
            lineToRelative(-10.425f, -5.675f)
            lineToRelative(2.1f, -1.15f)
            lineToRelative(8.325f, 4.55f)
            lineToRelative(8.325f, -4.55f)
            lineToRelative(2.1f, 1.15f)
            lineToRelative(-10.425f, 5.675f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12f, 11.725f)
            lineToRelative(6.825f, -3.725f)
            lineToRelative(-6.825f, -3.725f)
            lineToRelative(-6.825f, 3.725f)
            lineToRelative(6.825f, 3.725f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 8f)
            lineToRelative(0f, 0f)
            lineToRelative(0f, 0f)
            lineToRelative(0f, 0f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 2f)
            lineTo(1f, 8f)
            lineToRelative(11f, 6f)
            lineToRelative(11f, -6f)
            lineTo(12f, 2f)
            close()
            moveTo(5.175f, 8f)
            lineToRelative(6.825f, -3.725f)
            lineToRelative(6.825f, 3.725f)
            lineToRelative(-6.825f, 3.725f)
            lineToRelative(-6.825f, -3.725f)
            close()
        }
    }.build()
}