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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.HashTag: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.HashTag",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(360f, 640f)
            lineToRelative(-33f, 131f)
            quadToRelative(-3f, 13f, -13f, 21f)
            reflectiveQuadToRelative(-24f, 8f)
            quadToRelative(-19f, 0f, -31f, -15f)
            reflectiveQuadToRelative(-7f, -33f)
            lineToRelative(28f, -112f)
            lineTo(171f, 640f)
            quadToRelative(-20f, 0f, -32f, -15.5f)
            reflectiveQuadToRelative(-7f, -34.5f)
            quadToRelative(3f, -14f, 14f, -22f)
            reflectiveQuadToRelative(25f, -8f)
            horizontalLineToRelative(129f)
            lineToRelative(40f, -160f)
            lineTo(231f, 400f)
            quadToRelative(-20f, 0f, -32f, -15.5f)
            reflectiveQuadToRelative(-7f, -34.5f)
            quadToRelative(3f, -14f, 14f, -22f)
            reflectiveQuadToRelative(25f, -8f)
            horizontalLineToRelative(129f)
            lineToRelative(33f, -131f)
            quadToRelative(3f, -13f, 13f, -21f)
            reflectiveQuadToRelative(24f, -8f)
            quadToRelative(19f, 0f, 31f, 15f)
            reflectiveQuadToRelative(7f, 33f)
            lineToRelative(-28f, 112f)
            horizontalLineToRelative(160f)
            lineToRelative(33f, -131f)
            quadToRelative(3f, -13f, 13f, -21f)
            reflectiveQuadToRelative(24f, -8f)
            quadToRelative(19f, 0f, 31f, 15f)
            reflectiveQuadToRelative(7f, 33f)
            lineToRelative(-28f, 112f)
            horizontalLineToRelative(109f)
            quadToRelative(20f, 0f, 32f, 15.5f)
            reflectiveQuadToRelative(7f, 34.5f)
            quadToRelative(-3f, 14f, -14f, 22f)
            reflectiveQuadToRelative(-25f, 8f)
            lineTo(660f, 400f)
            lineToRelative(-40f, 160f)
            horizontalLineToRelative(109f)
            quadToRelative(20f, 0f, 32f, 15.5f)
            reflectiveQuadToRelative(7f, 34.5f)
            quadToRelative(-3f, 14f, -14f, 22f)
            reflectiveQuadToRelative(-25f, 8f)
            lineTo(600f, 640f)
            lineToRelative(-33f, 131f)
            quadToRelative(-3f, 13f, -13f, 21f)
            reflectiveQuadToRelative(-24f, 8f)
            quadToRelative(-19f, 0f, -31f, -15f)
            reflectiveQuadToRelative(-7f, -33f)
            lineToRelative(28f, -112f)
            lineTo(360f, 640f)
            close()
            moveTo(380f, 560f)
            horizontalLineToRelative(160f)
            lineToRelative(40f, -160f)
            lineTo(420f, 400f)
            lineToRelative(-40f, 160f)
            close()
        }
    }.build()
}
