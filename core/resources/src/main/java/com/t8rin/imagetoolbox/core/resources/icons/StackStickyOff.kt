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

val Icons.Outlined.StackStickyOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.StackStickyOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.825f, 4f)
            lineToRelative(0.175f, 0.75f)
            curveToRelative(0.083f, 0.333f, 0.237f, 0.563f, 0.463f, 0.688f)
            curveToRelative(0.225f, 0.125f, 0.462f, 0.171f, 0.712f, 0.138f)
            reflectiveCurveToRelative(0.467f, -0.15f, 0.65f, -0.35f)
            curveToRelative(0.183f, -0.2f, 0.233f, -0.467f, 0.15f, -0.8f)
            lineToRelative(-0.175f, -0.775f)
            curveToRelative(-0.133f, -0.533f, -0.417f, -0.963f, -0.85f, -1.288f)
            reflectiveCurveToRelative(-0.917f, -0.438f, -1.45f, -0.337f)
            lineToRelative(-9.38f, 1.664f)
            lineToRelative(1.729f, 1.729f)
            lineToRelative(7.976f, -1.418f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.412f, 7.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineToRelative(-10.975f)
            curveToRelative(-0.183f, 0f, -0.355f, 0.029f, -0.522f, 0.072f)
            lineToRelative(1.928f, 1.928f)
            horizontalLineToRelative(9.569f)
            verticalLineToRelative(7f)
            horizontalLineToRelative(-2.569f)
            lineToRelative(2.785f, 2.785f)
            lineToRelative(1.21f, -1.21f)
            curveToRelative(0.183f, -0.183f, 0.325f, -0.396f, 0.425f, -0.638f)
            curveToRelative(0.1f, -0.242f, 0.15f, -0.496f, 0.15f, -0.763f)
            verticalLineToRelative(-7.175f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(2.49f, 3.844f)
            curveToRelative(-0.395f, -0.395f, -1.036f, -0.395f, -1.431f, 0f)
            curveToRelative(-0.395f, 0.395f, -0.395f, 1.036f, 0f, 1.431f)
            lineToRelative(0.965f, 0.965f)
            curveToRelative(0.001f, 0.004f, 0f, 0.007f, 0.001f, 0.01f)
            lineToRelative(1.525f, 8.625f)
            curveToRelative(0.05f, 0.283f, 0.183f, 0.504f, 0.4f, 0.662f)
            curveToRelative(0.217f, 0.158f, 0.467f, 0.213f, 0.75f, 0.163f)
            reflectiveCurveToRelative(0.5f, -0.188f, 0.65f, -0.413f)
            curveToRelative(0.15f, -0.225f, 0.2f, -0.479f, 0.15f, -0.762f)
            lineToRelative(-1.016f, -5.826f)
            lineToRelative(2.516f, 2.516f)
            verticalLineToRelative(8.784f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(7.175f)
            curveToRelative(0.267f, 0f, 0.521f, -0.05f, 0.763f, -0.15f)
            curveToRelative(0.16f, -0.066f, 0.302f, -0.157f, 0.437f, -0.26f)
            lineToRelative(1.244f, 1.244f)
            curveToRelative(0.395f, 0.395f, 1.036f, 0.395f, 1.431f, 0f)
            reflectiveCurveToRelative(0.395f, -1.036f, 0f, -1.431f)
            lineTo(2.49f, 3.844f)
            close()
            moveTo(9f, 20f)
            verticalLineToRelative(-6.784f)
            lineToRelative(6.785f, 6.784f)
            horizontalLineToRelative(-6.785f)
            close()
        }
    }.build()
}
