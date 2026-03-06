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

val Icons.Outlined.PaletteBox: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.PaletteBox",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 20f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(6f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(16f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(4f)
            close()
            moveTo(17.5f, 8.675f)
            horizontalLineToRelative(2.5f)
            verticalLineToRelative(-2.675f)
            horizontalLineToRelative(-2.5f)
            verticalLineToRelative(2.675f)
            close()
            moveTo(17.5f, 13.325f)
            horizontalLineToRelative(2.5f)
            verticalLineToRelative(-2.65f)
            horizontalLineToRelative(-2.5f)
            verticalLineToRelative(2.65f)
            close()
            moveTo(4f, 18f)
            horizontalLineToRelative(11.5f)
            verticalLineTo(6f)
            horizontalLineTo(4f)
            verticalLineToRelative(12f)
            close()
            moveTo(17.5f, 18f)
            horizontalLineToRelative(2.5f)
            verticalLineToRelative(-2.675f)
            horizontalLineToRelative(-2.5f)
            verticalLineToRelative(2.675f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(9.75f, 15f)
            curveToRelative(-0.7f, 0f, -1.296f, -0.242f, -1.788f, -0.726f)
            curveToRelative(-0.492f, -0.484f, -0.738f, -1.074f, -0.738f, -1.768f)
            curveToRelative(0f, -0.332f, 0.064f, -0.649f, 0.193f, -0.951f)
            curveToRelative(0.129f, -0.303f, 0.312f, -0.57f, 0.549f, -0.801f)
            lineToRelative(1.784f, -1.753f)
            lineToRelative(1.784f, 1.753f)
            curveToRelative(0.237f, 0.232f, 0.42f, 0.499f, 0.549f, 0.801f)
            curveToRelative(0.129f, 0.303f, 0.193f, 0.62f, 0.193f, 0.951f)
            curveToRelative(0f, 0.695f, -0.246f, 1.284f, -0.738f, 1.768f)
            reflectiveCurveToRelative(-1.088f, 0.726f, -1.788f, 0.726f)
            close()
        }
    }.build()
}

val Icons.Rounded.PaletteBox: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.PaletteBox",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.412f, 4.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(4f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(16f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(6f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(11.538f, 14.274f)
            curveToRelative(-0.492f, 0.484f, -1.088f, 0.726f, -1.788f, 0.726f)
            reflectiveCurveToRelative(-1.296f, -0.242f, -1.788f, -0.726f)
            curveToRelative(-0.492f, -0.484f, -0.738f, -1.074f, -0.738f, -1.768f)
            curveToRelative(0f, -0.332f, 0.064f, -0.649f, 0.193f, -0.951f)
            reflectiveCurveToRelative(0.312f, -0.57f, 0.549f, -0.801f)
            lineToRelative(1.784f, -1.753f)
            lineToRelative(1.784f, 1.753f)
            curveToRelative(0.237f, 0.232f, 0.42f, 0.499f, 0.549f, 0.801f)
            reflectiveCurveToRelative(0.193f, 0.62f, 0.193f, 0.951f)
            curveToRelative(0f, 0.695f, -0.246f, 1.284f, -0.738f, 1.768f)
            close()
            moveTo(20f, 18f)
            horizontalLineToRelative(-2.5f)
            verticalLineToRelative(-2.675f)
            horizontalLineToRelative(2.5f)
            verticalLineToRelative(2.675f)
            close()
            moveTo(20f, 13.325f)
            horizontalLineToRelative(-2.5f)
            verticalLineToRelative(-2.65f)
            horizontalLineToRelative(2.5f)
            verticalLineToRelative(2.65f)
            close()
            moveTo(20f, 8.675f)
            horizontalLineToRelative(-2.5f)
            verticalLineToRelative(-2.675f)
            horizontalLineToRelative(2.5f)
            verticalLineToRelative(2.675f)
            close()
        }
    }.build()
}