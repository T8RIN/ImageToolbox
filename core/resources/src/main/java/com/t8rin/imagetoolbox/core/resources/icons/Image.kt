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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Image: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Image",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(200f, 760f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-560f)
            lineTo(200f, 200f)
            verticalLineToRelative(560f)
            close()
            moveTo(200f, 760f)
            verticalLineToRelative(-560f)
            verticalLineToRelative(560f)
            close()
            moveTo(280f, 680f)
            horizontalLineToRelative(400f)
            quadToRelative(12f, 0f, 18f, -11f)
            reflectiveQuadToRelative(-2f, -21f)
            lineTo(586f, 501f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineTo(450f, 640f)
            lineToRelative(-74f, -99f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineToRelative(-80f, 107f)
            quadToRelative(-8f, 10f, -2f, 21f)
            reflectiveQuadToRelative(18f, 11f)
            close()
        }
    }.build()
}

val Icons.Rounded.Image: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Image",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(280f, 680f)
            horizontalLineToRelative(400f)
            quadToRelative(12f, 0f, 18f, -11f)
            reflectiveQuadToRelative(-2f, -21f)
            lineTo(586f, 501f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineTo(450f, 640f)
            lineToRelative(-74f, -99f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineToRelative(-80f, 107f)
            quadToRelative(-8f, 10f, -2f, 21f)
            reflectiveQuadToRelative(18f, 11f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Image: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Image",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 21f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            curveToRelative(0.392f, -0.392f, 0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            curveToRelative(-0.392f, 0.392f, -0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(5f)
            close()
            moveTo(5f, 19f)
            horizontalLineToRelative(14f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            verticalLineToRelative(14f)
            close()
            moveTo(5f, 19f)
            verticalLineTo(5f)
            verticalLineToRelative(14f)
            close()
            moveTo(7f, 17f)
            horizontalLineToRelative(10f)
            curveToRelative(0.2f, 0f, 0.35f, -0.092f, 0.45f, -0.275f)
            reflectiveCurveToRelative(0.083f, -0.358f, -0.05f, -0.525f)
            lineToRelative(-2.75f, -3.675f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2.6f, 3.475f)
            lineToRelative(-1.85f, -2.475f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2f, 2.675f)
            curveToRelative(-0.133f, 0.167f, -0.15f, 0.342f, -0.05f, 0.525f)
            reflectiveCurveToRelative(0.25f, 0.275f, 0.45f, 0.275f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(-14f)
            close()
        }
    }.build()
}