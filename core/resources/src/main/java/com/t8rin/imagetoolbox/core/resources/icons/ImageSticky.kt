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

val Icons.Outlined.ImageSticky: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ImageSticky",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.412f, 3.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(5f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(10.175f)
            curveToRelative(0.267f, 0f, 0.521f, -0.05f, 0.763f, -0.15f)
            curveToRelative(0.242f, -0.1f, 0.454f, -0.242f, 0.638f, -0.425f)
            lineToRelative(3.85f, -3.85f)
            curveToRelative(0.183f, -0.183f, 0.325f, -0.396f, 0.425f, -0.638f)
            curveToRelative(0.1f, -0.242f, 0.15f, -0.496f, 0.15f, -0.763f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(19f, 15f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(2f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(10f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 19f)
            verticalLineTo(5f)
            verticalLineToRelative(14f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(8.281f, 13.968f)
            curveToRelative(-0.248f, 0f, -0.434f, -0.114f, -0.558f, -0.341f)
            reflectiveCurveToRelative(-0.103f, -0.444f, 0.062f, -0.651f)
            lineToRelative(1.395f, -1.86f)
            curveToRelative(0.124f, -0.165f, 0.289f, -0.248f, 0.496f, -0.248f)
            reflectiveCurveToRelative(0.372f, 0.083f, 0.496f, 0.248f)
            lineToRelative(1.209f, 1.612f)
            lineToRelative(1.829f, -2.449f)
            curveToRelative(0.124f, -0.165f, 0.289f, -0.248f, 0.496f, -0.248f)
            reflectiveCurveToRelative(0.372f, 0.083f, 0.496f, 0.248f)
            lineToRelative(2.015f, 2.697f)
            curveToRelative(0.165f, 0.207f, 0.186f, 0.424f, 0.062f, 0.651f)
            reflectiveCurveToRelative(-0.31f, 0.341f, -0.558f, 0.341f)
            horizontalLineToRelative(-7.439f)
            close()
        }
    }.build()
}
